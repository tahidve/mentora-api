package vn.mentora.mentora_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.mentora.mentora_api.domain.Booking;
import vn.mentora.mentora_api.domain.Discount;
import vn.mentora.mentora_api.domain.Invoice;
import vn.mentora.mentora_api.domain.Meeting;
import vn.mentora.mentora_api.domain.Mentee;
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

    private final BookingRepository bookingRepository;
    private final MenteeRepository menteeRepository;
    private final PlanRepository planRepository;
    private final DiscountRepository discountRepository;
    private final SlotRepository slotRepository;
    private final DiscountService discountService;
    private final PlanRegistrationRepository planRegistrationRepository;
    private final InvoiceRepository invoiceRepository;
    private final MeetingRepository meetingRepository;

    public BookingService(BookingRepository bookingRepository, MenteeRepository menteeRepository,
            PlanRepository planRepository, DiscountRepository discountRepository, SlotRepository slotRepository,
            DiscountService discountService, PlanRegistrationRepository planRegistrationRepository,
            InvoiceRepository invoiceRepository, MeetingRepository meetingRepository) {
        this.bookingRepository = bookingRepository;
        this.menteeRepository = menteeRepository;
        this.planRepository = planRepository;
        this.discountRepository = discountRepository;
        this.slotRepository = slotRepository;
        this.discountService = discountService;
        this.planRegistrationRepository = planRegistrationRepository;
        this.invoiceRepository = invoiceRepository;
        this.meetingRepository = meetingRepository;
    }

    @Transactional
    public ResBookingDTO createBooking(String email, ReqCreateBookingDTO dto) throws IdInvalidException {
        Mentee mentee = this.menteeRepository.findByUserEmail(email)
                .orElseThrow(() -> new IdInvalidException("Không tồn tại mentee này"));

        Plan plan = this.planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new IdInvalidException("Không tồn tại plan này"));

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

        Invoice invoice = new Invoice();
        invoice.setAmountTotal(amountTotal);
        invoice.setAmountSubtotal(amountSubtotal);
        invoice.setMentee(mentee);
        invoice.setPlanRegistration(registration);
        this.invoiceRepository.save(invoice);

        Meeting meeting = new Meeting();
        meeting.setInvoice(invoice);
        meeting.setSlot(slot);
        meeting.setDate(slot.getDate());
        meeting.setStartTime(slot.getStartTime());
        meeting.setEndTime(slot.getEndTime());
        meeting.setStatus(MeetingStatusEnum.PENDING); // Chờ mentor xác nhận, đặt link
        meeting = meetingRepository.save(meeting);

        Booking booking = new Booking();
        booking.setMentee(mentee);
        booking.setPlan(plan);
        booking.setPlanRegistration(registration);
        this.bookingRepository.save(booking);

        slot.setStatus(SlotStatusEnum.BOOKED);
        this.slotRepository.save(slot);

        // Build response DTO (trong transaction scope — có đủ slot, invoice, plan,
        // booking trong tay)
        ResBookingDTO res = new ResBookingDTO();
        res.setBookingId(booking.getBookingId());
        res.setInvoiceId(invoice.getInvoiceId());
        res.setMeetingId(meeting.getMeetingId());
        res.setAmountSubtotal(amountSubtotal);
        res.setAmountTotal(amountTotal);
        res.setDiscountApplied(discount != null ? discount.getDiscountName() : null);
        res.setSlotDate(slot.getDate());
        res.setStartTime(slot.getStartTime());
        res.setEndTime(slot.getEndTime());
        res.setPaymentStatus(invoice.getPaymentStatus().name());
        res.setBookingStatus(booking.getStatus().name());

        // Plan name: PlanSession có sessionName, PlanMentorship có tên riêng
        if (plan instanceof PlanSession ps) {
            res.setPlanName(ps.getSessionName());
        } else if (plan instanceof PlanMentorship pm) {
            res.setPlanName(pm.getPlanName());
        }

        // Mentor name lấy từ User (firstName + lastName)
        String mentorName = plan.getMentor().getUser().getFirstName() + " " + plan.getMentor().getUser().getLastName();
        res.setMentorName(mentorName);

        return res;
    }

    @Transactional
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

        return res;
    }

}
