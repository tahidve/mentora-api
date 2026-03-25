package vn.mentora.mentora_api.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.mentora.mentora_api.domain.Booking;
import vn.mentora.mentora_api.domain.Invoice;
import vn.mentora.mentora_api.domain.Meeting;
import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.repository.BookingRepository;
import vn.mentora.mentora_api.repository.InvoiceRepository;
import vn.mentora.mentora_api.repository.MeetingRepository;
import vn.mentora.mentora_api.repository.MentorRepository;
import vn.mentora.mentora_api.util.constant.BookingStatusEnum;
import vn.mentora.mentora_api.util.constant.MeetingStatusEnum;
import vn.mentora.mentora_api.util.constant.PaymentStatusEnum;
import vn.mentora.mentora_api.util.constant.SlotStatusEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class MeetingService {

    private final MentorRepository mentorRepository;
    private final MeetingRepository meetingRepository;
    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;

    public MeetingService(MentorRepository mentorRepository, MeetingRepository meetingRepository,
            BookingRepository bookingRepository, InvoiceRepository invoiceRepository) {
        this.meetingRepository = meetingRepository;
        this.mentorRepository = mentorRepository;
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public Meeting setMeetingLocation(String location, Long meetingId, String email) throws IdInvalidException {
        // check mentor có phải own meeting này hay không
        Meeting meeting = this.meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IdInvalidException("Không tồn tại meeting này"));

        if (!meeting.getSlot().getMentor().getUser().getEmail().equals(email)) {
            throw new IdInvalidException("Mentor không sở hữu meeting này");
        }

        if (meeting.getStatus() != MeetingStatusEnum.PENDING && meeting.getStatus() != MeetingStatusEnum.SCHEDULED) {
            throw new IdInvalidException("Meeting đã được hủy hoặc đã được hoàn thành");
        }

        // set meeting location
        meeting.setLocation(location);
        meeting.setStatus(MeetingStatusEnum.SCHEDULED);
        return meetingRepository.save(meeting);
    }

    @Transactional
    public Meeting completeMeeting(Long meetingId, String email) throws IdInvalidException {
        Meeting meeting = this.meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IdInvalidException("Meeting không tồn tại"));

        if (!meeting.getSlot().getMentor().getUser().getEmail().equals(email)) {
            throw new IdInvalidException("Meeting không thuộc về mentor");
        }

        if (meeting.getStatus() != MeetingStatusEnum.SCHEDULED) {
            throw new IdInvalidException("Meeting chưa được lên lịch");
        }

        meeting.setStatus(MeetingStatusEnum.COMPLETED);

        Invoice invoice = meeting.getInvoice();
        invoice.setPaymentStatus(PaymentStatusEnum.PAID);
        invoice.setPaidTime(Instant.now());

        Long planRegistrationId = invoice.getPlanRegistration().getRegistrationId();
        Booking booking = this.bookingRepository.findByPlanRegistrationRegistrationId(planRegistrationId)
                .orElseThrow(() -> new IdInvalidException("Không có booking cho meeting này?"));
        booking.setStatus(BookingStatusEnum.COMPLETED);

        this.invoiceRepository.save(invoice);
        this.bookingRepository.save(booking);

        Mentor mentor = meeting.getSlot().getMentor();
        mentor.setTotalMeetings(mentor.getTotalMeetings() + 1);

        this.mentorRepository.save(mentor);

        return this.meetingRepository.save(meeting);
    }

    @Transactional
    public Meeting cancelMeeting(Long meetingId, String email) throws IdInvalidException {
        Meeting meeting = this.meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IdInvalidException("Meeting không tồn tại"));

        if (!meeting.getSlot().getMentor().getUser().getEmail().equals(email)) {
            throw new IdInvalidException("Meeting không thuộc về mentor");
        }

        // Meeting đã tạo, mentor chưa lên lịch nhưng muốn hủy luôn hoặc hủy khi đã lên
        // lịch rồi
        if (meeting.getStatus() != MeetingStatusEnum.PENDING && meeting.getStatus() != MeetingStatusEnum.SCHEDULED) {
            throw new IdInvalidException("Chỉ có thể hủy meeting đã được lên lịch");
        }

        meeting.setStatus(MeetingStatusEnum.CANCELLED);
        meeting.getSlot().setStatus(SlotStatusEnum.AVAILABLE);
        meeting.getInvoice().setPaymentStatus(PaymentStatusEnum.CANCELLED);

        Long planRegistrationId = meeting.getInvoice().getPlanRegistration().getRegistrationId();
        Booking booking = this.bookingRepository.findByPlanRegistrationRegistrationId(planRegistrationId)
                .orElseThrow(() -> new IdInvalidException("Không có booking cho meeting này?"));
        booking.setStatus(BookingStatusEnum.CANCELLED);
        this.bookingRepository.save(booking);

        return this.meetingRepository.save(meeting);
    }

}
