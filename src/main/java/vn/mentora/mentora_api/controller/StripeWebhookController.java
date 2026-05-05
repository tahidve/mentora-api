package vn.mentora.mentora_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import vn.mentora.mentora_api.domain.*;
import vn.mentora.mentora_api.repository.*;
import vn.mentora.mentora_api.service.*;
import vn.mentora.mentora_api.util.constant.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/webhooks")
public class StripeWebhookController {

    private final StripeService stripeService;
    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final MeetingRepository meetingRepository;
    private final EmailService emailService;

    public StripeWebhookController(StripeService stripeService, InvoiceRepository invoiceRepository,
            BookingRepository bookingRepository, SlotRepository slotRepository,
            MeetingRepository meetingRepository, EmailService emailService) {
        this.stripeService = stripeService;
        this.invoiceRepository = invoiceRepository;
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.meetingRepository = meetingRepository;
        this.emailService = emailService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws EventDataObjectDeserializationException {

        String endpointSecret = stripeService.getWebhookSecret();
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            System.err.println("Webhook signature verification failed.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                // Fallback khi version API của Stripe và code Java không khớp nhau
                stripeObject = dataObjectDeserializer.deserializeUnsafe();
            }

            if (stripeObject != null) {
                Session session = (Session) stripeObject;
                processSuccessfulCheckout(session);
            }
        }

        return ResponseEntity.ok("Webhook received");
    }

    private void processSuccessfulCheckout(Session session) {
        String invoiceIdStr = session.getMetadata().get("invoice_id");
        String slotIdStr = session.getMetadata().get("slot_id");

        if (invoiceIdStr == null || slotIdStr == null) {
            return;
        }

        Long invoiceId = Long.parseLong(invoiceIdStr);
        Long slotId = Long.parseLong(slotIdStr);

        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        Slot slot = slotRepository.findById(slotId).orElse(null);

        if (invoice != null && slot != null) {
            Booking booking = bookingRepository
                    .findByPlanRegistrationRegistrationId(invoice.getPlanRegistration().getRegistrationId())
                    .orElse(null);

            // Update Invoice
            invoice.setPaymentStatus(PaymentStatusEnum.PAID);
            invoice.setPaidTime(Instant.now());
            invoice.setStripeCheckoutSessionId(session.getId());
            invoice.setStripePaymentIntentId(session.getPaymentIntent());
            invoiceRepository.save(invoice);

            if (booking != null) {
                booking.setStatus(BookingStatusEnum.CONFIRMED);
                bookingRepository.save(booking);
            }

            // Update Slot
            slot.setStatus(SlotStatusEnum.BOOKED);
            slotRepository.save(slot);

            // TẠO MEETING
            Meeting meeting = new Meeting();
            meeting.setInvoice(invoice);
            meeting.setSlot(slot);
            meeting.setDate(slot.getDate());
            meeting.setStartTime(slot.getStartTime());
            meeting.setEndTime(slot.getEndTime());
            meeting.setStatus(MeetingStatusEnum.PENDING);
            meetingRepository.save(meeting);

            // Gửi Email
            Mentor mentor = slot.getMentor();
            Mentee mentee = invoice.getMentee();
            String mentorName = mentor.getUser().getFirstName() + " " + mentor.getUser().getLastName();
            String menteeName = mentee.getUser().getFirstName() + " " + mentee.getUser().getLastName();
            String planName = "Mentorship Plan";
            if (booking != null && booking.getPlan() instanceof PlanSession) {
                planName = ((PlanSession) booking.getPlan()).getSessionName();
            }

            emailService.sendBookingConfirmToMentor(mentor.getUser().getEmail(), mentorName, menteeName, planName,
                    slot.getDate(), slot.getStartTime());
            emailService.sendBookingConfirmToMentee(mentee.getUser().getEmail(), menteeName, mentorName, planName,
                    slot.getDate(), slot.getStartTime(), invoice.getAmountTotal());

            System.out.println("WEBHOOK PROCESSED SUCCESSFUL: Meeting Created");
        }
    }
}
