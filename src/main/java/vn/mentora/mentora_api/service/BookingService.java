package vn.mentora.mentora_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.mentora.mentora_api.domain.Booking;
import vn.mentora.mentora_api.domain.Discount;
import vn.mentora.mentora_api.domain.Invoice;
import vn.mentora.mentora_api.domain.Mentee;
import vn.mentora.mentora_api.domain.Plan;
import vn.mentora.mentora_api.domain.PlanRegistration;
import vn.mentora.mentora_api.domain.PlanSession;
import vn.mentora.mentora_api.domain.Slot;
import vn.mentora.mentora_api.domain.request.ReqCreateBookingDTO;
import vn.mentora.mentora_api.domain.response.ResBookingDTO;
import vn.mentora.mentora_api.repository.BookingRepository;
import vn.mentora.mentora_api.repository.DiscountRepository;
import vn.mentora.mentora_api.repository.InvoiceRepository;
import vn.mentora.mentora_api.repository.MenteeRepository;
import vn.mentora.mentora_api.repository.PlanRegistrationRepository;
import vn.mentora.mentora_api.repository.PlanRepository;
import vn.mentora.mentora_api.repository.SlotRepository;
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

    public BookingService(BookingRepository bookingRepository, MenteeRepository menteeRepository,
            PlanRepository planRepository, DiscountRepository discountRepository, SlotRepository slotRepository,
            DiscountService discountService, PlanRegistrationRepository planRegistrationRepository,
            InvoiceRepository invoiceRepository) {
        this.bookingRepository = bookingRepository;
        this.menteeRepository = menteeRepository;
        this.planRepository = planRepository;
        this.discountRepository = discountRepository;
        this.slotRepository = slotRepository;
        this.discountService = discountService;
        this.planRegistrationRepository = planRegistrationRepository;
        this.invoiceRepository = invoiceRepository;
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

        if (slot.getDate().isBefore(LocalDate.now()))
            throw new IdInvalidException("Không thể đặt slot trong quá khứ");

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

        Booking booking = new Booking();
        booking.setMentee(mentee);
        booking.setPlan(plan);
        booking.setPlanRegistration(registration);
        this.bookingRepository.save(booking);

        slot.setStatus(SlotStatusEnum.BOOKED);
        this.slotRepository.save(slot);

        // Build response DTO (trong transaction scope — có đủ slot, invoice, plan, booking trong tay)
        ResBookingDTO res = new ResBookingDTO();
        res.setBookingId(booking.getBookingId());
        res.setInvoiceId(invoice.getInvoiceId());
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
        } else {
            res.setPlanName(plan.getDescription()); // fallback
        }

        // Mentor name lấy từ User (firstName + lastName)
        String mentorName = plan.getMentor().getUser().getFirstName() + " " + plan.getMentor().getUser().getLastName();
        res.setMentorName(mentorName);

        return res;
    }

}
