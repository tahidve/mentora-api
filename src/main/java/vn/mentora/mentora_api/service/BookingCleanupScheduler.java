package vn.mentora.mentora_api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.mentora.mentora_api.domain.Booking;
import vn.mentora.mentora_api.domain.Invoice;
import vn.mentora.mentora_api.domain.Slot;
import vn.mentora.mentora_api.repository.BookingRepository;
import vn.mentora.mentora_api.repository.InvoiceRepository;
import vn.mentora.mentora_api.repository.SlotRepository;
import vn.mentora.mentora_api.util.constant.BookingStatusEnum;
import vn.mentora.mentora_api.util.constant.PaymentStatusEnum;
import vn.mentora.mentora_api.util.constant.SlotStatusEnum;

@Component
public class BookingCleanupScheduler {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;

    private static final long RESERVATION_TIMEOUT_MINUTES = 15;

    public BookingCleanupScheduler(InvoiceRepository invoiceRepository,
            BookingRepository bookingRepository,
            SlotRepository slotRepository) {
        this.invoiceRepository = invoiceRepository;
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
    }

    @Scheduled(fixedRate = 300_000)
    @Transactional(rollbackFor = Exception.class)
    public void releaseExpiredReservations() {
        Instant cutoff = Instant.now().minus(RESERVATION_TIMEOUT_MINUTES, ChronoUnit.MINUTES);
        List<Invoice> expiredInvoices = invoiceRepository.findPendingCreatedBefore(cutoff);

        if (expiredInvoices.isEmpty()) {
            return; // Không có gì để dọn, thoát sớm
        }

        for (Invoice invoice : expiredInvoices) {
            Booking booking = bookingRepository
                    .findByPlanRegistrationRegistrationId(invoice.getPlanRegistration().getRegistrationId())
                    .orElse(null);

            if (booking == null || booking.getStatus() != BookingStatusEnum.PENDING_PAYMENT) {
                continue;
            }

            Long mentorId = booking.getPlan().getMentor().getMentorId();
            List<Slot> reservedSlots = slotRepository.findByMentorMentorIdAndStatus(
                    mentorId, SlotStatusEnum.RESERVED);

            invoice.setPaymentStatus(PaymentStatusEnum.CANCELLED);
            booking.setStatus(BookingStatusEnum.CANCELLED);
            invoiceRepository.save(invoice);
            bookingRepository.save(booking);

            for (Slot slot : reservedSlots) {
                slot.setStatus(SlotStatusEnum.AVAILABLE);
                slotRepository.save(slot);
            }

            System.out.println("[CLEANUP] Đã hủy Invoice #" + invoice.getInvoiceId()
                    + " + giải phóng " + reservedSlots.size() + " Slot(s) của Mentor #" + mentorId
                    + " vì quá " + RESERVATION_TIMEOUT_MINUTES + " phút không thanh toán.");
        }
    }
}
