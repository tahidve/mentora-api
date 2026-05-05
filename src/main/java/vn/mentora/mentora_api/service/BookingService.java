package vn.mentora.mentora_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import vn.mentora.mentora_api.domain.Booking;
import vn.mentora.mentora_api.domain.Discount;
import vn.mentora.mentora_api.domain.Invoice;
import vn.mentora.mentora_api.domain.Meeting;
import vn.mentora.mentora_api.domain.Mentee;
import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.Plan;
import vn.mentora.mentora_api.domain.PlanMentorship;
import vn.mentora.mentora_api.domain.PlanRegistration;
import vn.mentora.mentora_api.domain.PlanSession;
import vn.mentora.mentora_api.domain.Slot;
import vn.mentora.mentora_api.domain.request.ReqCreateBookingDTO;
import vn.mentora.mentora_api.domain.response.ResBookingDTO;
import vn.mentora.mentora_api.repository.BookingRepository;
import vn.mentora.mentora_api.repository.DiscountRepository;
import vn.mentora.mentora_api.repository.InvoiceRepository;
import vn.mentora.mentora_api.repository.MeetingRepository;
import vn.mentora.mentora_api.repository.MenteeRepository;
import vn.mentora.mentora_api.repository.PlanRegistrationRepository;
import vn.mentora.mentora_api.repository.PlanRepository;
import vn.mentora.mentora_api.repository.SlotRepository;
import vn.mentora.mentora_api.util.constant.BookingStatusEnum;
import vn.mentora.mentora_api.util.constant.MeetingStatusEnum;
import vn.mentora.mentora_api.util.constant.PaymentStatusEnum;
import vn.mentora.mentora_api.util.constant.SlotStatusEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class BookingService {

    @Value("${stripe.platform-fee-percent}")
    private int platformFeePercent;

    private final BookingRepository bookingRepository;
    private final MenteeRepository menteeRepository;
    private final PlanRepository planRepository;
    private final DiscountRepository discountRepository;
    private final SlotRepository slotRepository;
    private final DiscountService discountService;
    private final PlanRegistrationRepository planRegistrationRepository;
    private final InvoiceRepository invoiceRepository;
    private final MeetingRepository meetingRepository;
    private final EmailService emailService;
    private final StripeService stripeService;

    public BookingService(BookingRepository bookingRepository, MenteeRepository menteeRepository,
            PlanRepository planRepository, DiscountRepository discountRepository, SlotRepository slotRepository,
            DiscountService discountService, PlanRegistrationRepository planRegistrationRepository,
            InvoiceRepository invoiceRepository, MeetingRepository meetingRepository,
            EmailService emailService, StripeService stripeService) {
        this.bookingRepository = bookingRepository;
        this.menteeRepository = menteeRepository;
        this.planRepository = planRepository;
        this.discountRepository = discountRepository;
        this.slotRepository = slotRepository;
        this.discountService = discountService;
        this.planRegistrationRepository = planRegistrationRepository;
        this.invoiceRepository = invoiceRepository;
        this.meetingRepository = meetingRepository;
        this.emailService = emailService;
        this.stripeService = stripeService;
    }

    @Transactional(rollbackFor = Exception.class)
    public String createBooking(String email, ReqCreateBookingDTO dto) throws IdInvalidException {
        Mentee mentee = this.menteeRepository.findByUserEmail(email)
                .orElseThrow(() -> new IdInvalidException("Không tồn tại mentee này"));

        Plan plan = this.planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new IdInvalidException("Không tồn tại plan này"));

        Mentor mentor = plan.getMentor();
        if (!mentor.isStripeOnboarded()) {
            throw new IdInvalidException("Mentor chưa liên kết tài khoản nhận tiền, không thể đặt lịch");
        }

        Discount discount = null;
        if (dto.getDiscountId() != null) {
            discount = this.discountRepository.findById(dto.getDiscountId())
                    .orElseThrow(() -> new IdInvalidException("Không tồn tại discount này"));
        }

        Slot slot = this.slotRepository.findById(dto.getSlotId())
                .orElseThrow(() -> new IdInvalidException("Không tồn tại slot này"));

        if (slot.getStatus() != SlotStatusEnum.AVAILABLE)
            throw new IdInvalidException("Slot đã được đặt");

        if (!slot.getDate().isAfter(LocalDate.now().plusDays(1))) {
            throw new IdInvalidException(
                    "Chỉ có thể đặt slot ít nhất 24h trước khi bắt đầu. Slot ngày " + slot.getDate()
                            + " là không hợp lệ (quá gần hoặc trong quá khứ).");
        }

        if (!slot.getMentor().getMentorId().equals(plan.getMentor().getMentorId()))
            throw new IdInvalidException("Slot không thuộc mentor của plan này");

        // lấy giá gốc
        BigDecimal amountSubtotal = plan.getCharge();
        BigDecimal amountTotal = amountSubtotal;

        // áp mã giảm giá nếu có
        if (discount != null) {
            amountTotal = this.discountService.validateAndApplyDiscount(dto.getDiscountId(), amountSubtotal);
            discount.setUsedCount(discount.getUsedCount() + 1);
            this.discountRepository.save(discount);
        }

        // tạo plan registration
        PlanRegistration registration = new PlanRegistration();
        registration.setMessage(dto.getMessage());
        registration.setDiscount(discount);
        this.planRegistrationRepository.save(registration);

        // tạo Invoice — ghi nhận phí sàn và tiền mentor nhận
        Invoice invoice = new Invoice();
        invoice.setAmountTotal(amountTotal);
        invoice.setAmountSubtotal(amountSubtotal);
        invoice.setMentee(mentee);
        invoice.setPlanRegistration(registration);
        invoice.setCurrency("USD");
        // Tính phí sàn
        BigDecimal fee = amountTotal
                .multiply(BigDecimal.valueOf(platformFeePercent))
                .divide(BigDecimal.valueOf(100));
        invoice.setPlatformFee(fee);
        invoice.setNetAmount(amountTotal.subtract(fee));
        this.invoiceRepository.save(invoice);

        Booking booking = new Booking();
        booking.setMentee(mentee);
        booking.setPlan(plan);
        booking.setPlanRegistration(registration);
        booking.setStatus(BookingStatusEnum.PENDING_PAYMENT);
        this.bookingRepository.save(booking);

        // slot RESERVED — Webhook mới đổi sang BOOKED sau khi thu
        // tiền xong
        slot.setStatus(SlotStatusEnum.RESERVED);
        this.slotRepository.save(slot);

        // Gọi Stripe tạo link Checkout
        long amountInCents = amountTotal.multiply(BigDecimal.valueOf(100)).longValue();

        // unproxy để instanceof không bị trả false với Hibernate Proxy
        Plan realPlan = (Plan) org.hibernate.Hibernate.unproxy(plan);
        String planName = (realPlan instanceof PlanSession ps)
                ? ps.getSessionName()
                : "Mentorship Plan";

        String checkoutUrl = this.stripeService.createCheckoutSession(
                planName,
                amountInCents,
                "usd",
                invoice.getInvoiceId(),
                slot.getSlotId(),
                mentor.getStripeAccountId());

        return checkoutUrl;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResBookingDTO cancelBooking(String email, Long bookingId) throws IdInvalidException {
        Booking booking = this.bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdInvalidException("Booking không tồn tại"));

        if (booking.getStatus() != BookingStatusEnum.CONFIRMED) {
            throw new IdInvalidException("Chỉ có thể hủy Booking ở trạng thái CONFIRMED");
        }

        // validate booking thuộc mentee này
        Mentee mentee = this.menteeRepository.findByUserEmail(email)
                .orElseThrow(() -> new IdInvalidException("Không tồn tại mentee"));

        if (!booking.getMentee().getMenteeId().equals(mentee.getMenteeId())) {
            throw new IdInvalidException("Booking không thuộc về bạn");
        }

        // Navigate: Booking → PlanRegistration → Invoice (qua Repository vì
        // unidirectional)
        PlanRegistration registration = booking.getPlanRegistration();
        Invoice invoice = this.invoiceRepository.findByPlanRegistrationRegistrationId(registration.getRegistrationId())
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Invoice cho booking này"));

        // Navigate: Invoice → Meeting (qua Repository)
        Meeting meeting = this.meetingRepository.findByInvoiceInvoiceId(invoice.getInvoiceId())
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Meeting cho booking này"));

        // Check ≥24h trước buổi học (dùng snapshot trên meeting)
        Slot slot = meeting.getSlot();
        if (!meeting.getDate().isAfter(LocalDate.now().plusDays(1))) {
            throw new IdInvalidException(
                    "Không thể hủy booking trong vòng 24h trước buổi học (ngày " + meeting.getDate() + ")");
        }

        // === Update statuses ===
        booking.setStatus(BookingStatusEnum.CANCELLED);
        invoice.setPaymentStatus(PaymentStatusEnum.CANCELLED);
        meeting.setStatus(MeetingStatusEnum.CANCELLED);
        slot.setStatus(SlotStatusEnum.AVAILABLE);

        this.bookingRepository.save(booking);
        this.invoiceRepository.save(invoice);
        this.meetingRepository.save(meeting);
        this.slotRepository.save(slot);

        // Build response DTO
        ResBookingDTO res = new ResBookingDTO();
        res.setBookingId(booking.getBookingId());
        res.setInvoiceId(invoice.getInvoiceId());
        res.setMeetingId(meeting.getMeetingId());
        res.setAmountSubtotal(invoice.getAmountSubtotal());
        res.setAmountTotal(invoice.getAmountTotal());
        res.setSlotDate(meeting.getDate());
        res.setStartTime(meeting.getStartTime());
        res.setEndTime(meeting.getEndTime());
        res.setPaymentStatus(invoice.getPaymentStatus().name());
        res.setBookingStatus(booking.getStatus().name());

        // Plan name
        Plan plan = booking.getPlan();
        if (plan instanceof PlanSession ps) {
            res.setPlanName(ps.getSessionName());
        } else if (plan instanceof PlanMentorship pm) {
            res.setPlanName(pm.getPlanName());
        }

        // Mentor name
        String mentorName = plan.getMentor().getUser().getFirstName() + " "
                + plan.getMentor().getUser().getLastName();
        res.setMentorName(mentorName);

        // Email Mentor: Mentee đã hủy
        // Plan name đã được set ở trên (res.getPlanName())
        this.emailService.sendBookingCancelledToMentor(
                plan.getMentor().getUser().getEmail(),
                mentorName,
                mentee.getUser().getFirstName() + " " + mentee.getUser().getLastName(),
                res.getPlanName(),
                meeting.getDate());

        return res;
    }

}
