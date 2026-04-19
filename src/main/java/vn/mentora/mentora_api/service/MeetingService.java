package vn.mentora.mentora_api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.named.ResultMappingMementoNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.mentora.mentora_api.domain.Booking;
import vn.mentora.mentora_api.domain.Invoice;
import vn.mentora.mentora_api.domain.Meeting;
import vn.mentora.mentora_api.domain.Mentee;
import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.Plan;
import vn.mentora.mentora_api.domain.PlanMentorship;
import vn.mentora.mentora_api.domain.PlanSession;
import vn.mentora.mentora_api.domain.response.ResMeetingDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
import vn.mentora.mentora_api.repository.BookingRepository;
import vn.mentora.mentora_api.repository.InvoiceRepository;
import vn.mentora.mentora_api.repository.MeetingRepository;
import vn.mentora.mentora_api.repository.MenteeRepository;
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
    private final MenteeRepository menteeRepository;
    private final EmailService emailService;

    public MeetingService(MentorRepository mentorRepository, MeetingRepository meetingRepository,
            BookingRepository bookingRepository, InvoiceRepository invoiceRepository,
            MenteeRepository menteeRepository, EmailService emailService) {
        this.meetingRepository = meetingRepository;
        this.mentorRepository = mentorRepository;
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
        this.menteeRepository = menteeRepository;
        this.emailService = emailService;
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
        Meeting saved = meetingRepository.save(meeting);

        // Email cho Mentee: link họp đã sẵn sàng
        String menteeName = meeting.getInvoice().getMentee().getUser().getFirstName() + " "
                + meeting.getInvoice().getMentee().getUser().getLastName();
        String mentorName = meeting.getSlot().getMentor().getUser().getFirstName() + " "
                + meeting.getSlot().getMentor().getUser().getLastName();
        this.emailService.sendMeetingLocationToMentee(
                meeting.getInvoice().getMentee().getUser().getEmail(),
                menteeName, mentorName, location,
                meeting.getDate(), meeting.getStartTime());

        return saved;
    }

    @Transactional
    public Meeting completeMeeting(Long meetingId, String email) throws IdInvalidException {
        Meeting meeting = this.meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IdInvalidException("Meeting không tồn tại"));

        if (!meeting.getSlot().getMentor().getUser().getEmail().equals(email)) {
            throw new IdInvalidException("Meeting không thuộc về mentor");
        }

        if (meeting.getStatus() != MeetingStatusEnum.SCHEDULED) {
            throw new IdInvalidException("Meeting chưa được lên lịch hoặc đã bị hủy");
        }

        // CHỐNG GIAN LẬN: Mentor chỉ được bấm "Complete" sau khi buổi học đã kết thúc
        // theo lịch.
        // Ghép date + endTime thành LocalDateTime để so sánh với thời điểm hiện tại.
        // Ví dụ: Lịch học kết thúc 10:00 ngày 30/3 → phải sau 10:00 ngày 30/3 mới được
        // bấm.
        java.time.LocalDateTime scheduledEnd = java.time.LocalDateTime.of(meeting.getDate(), meeting.getEndTime());
        if (java.time.LocalDateTime.now().isBefore(scheduledEnd)) {
            throw new IdInvalidException(
                    "Buổi học chưa kết thúc! Bạn chỉ có thể hoàn thành sau " + meeting.getEndTime() + " ngày "
                            + meeting.getDate());
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

        Meeting saved = this.meetingRepository.save(meeting);

        // buổi học hoàn thành, mời đánh giá (async)
        String menteeName = meeting.getInvoice().getMentee().getUser().getFirstName() + " "
                + meeting.getInvoice().getMentee().getUser().getLastName();
        String mentorName = mentor.getUser().getFirstName() + " " + mentor.getUser().getLastName();
        this.emailService.sendMeetingCompletedToMentee(
                meeting.getInvoice().getMentee().getUser().getEmail(),
                menteeName, mentorName, meeting.getDate());

        return saved;
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

        Meeting saved = this.meetingRepository.save(meeting);

        // Mentor đã hủy meeting
        String menteeName = meeting.getInvoice().getMentee().getUser().getFirstName() + " "
                + meeting.getInvoice().getMentee().getUser().getLastName();
        String mentorName = meeting.getSlot().getMentor().getUser().getFirstName() + " "
                + meeting.getSlot().getMentor().getUser().getLastName();
        this.emailService.sendMeetingCancelledToMentee(
                meeting.getInvoice().getMentee().getUser().getEmail(),
                menteeName, mentorName, meeting.getDate(), meeting.getStartTime());

        return saved;
    }

    public ResultPaginationDTO fetchMeetings(Specification spec, Pageable pageable) throws IdInvalidException {
        Page<Meeting> page = this.meetingRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);

        List<ResMeetingDTO> meetingDTOs = new ArrayList<>();
        for (Meeting meeting : page.getContent()) {
            ResMeetingDTO dto = this.convertToMeetingDTO(meeting);
            meetingDTOs.add(dto);
        }

        result.setResult(meetingDTOs);

        return result;
    }

    public ResultPaginationDTO findByMenteeEmail(String email, Pageable pageable) throws IdInvalidException {
        Mentee mentee = this.menteeRepository.findByUserEmail(email)
                .orElseThrow(() -> new IdInvalidException("Mentee không tồn tại"));

        Page<Meeting> page = this.meetingRepository.findByInvoiceMenteeMenteeId(mentee.getMenteeId(), pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);

        List<ResMeetingDTO> meetingDTOs = new ArrayList<>();
        for (Meeting meeting : page.getContent()) {
            ResMeetingDTO dto = this.convertToMeetingDTO(meeting);
            meetingDTOs.add(dto);
        }

        result.setResult(meetingDTOs);

        return result;
    }

    public ResultPaginationDTO findByMentorEmail(String email, Pageable pageable) throws IdInvalidException {
        Mentor mentor = this.mentorRepository.findByUserEmail(email)
                .orElseThrow(() -> new IdInvalidException("Mentor không tồn tại"));

        Page<Meeting> page = this.meetingRepository.findBySlotMentorMentorId(mentor.getMentorId(), pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(page.getTotalElements());
        meta.setPages(page.getTotalPages());

        result.setMeta(meta);

        List<ResMeetingDTO> meetingDTOs = new ArrayList<>();
        for (Meeting meeting : page.getContent()) {
            ResMeetingDTO dto = this.convertToMeetingDTO(meeting);
            meetingDTOs.add(dto);
        }

        result.setResult(meetingDTOs);

        return result;
    }

    public ResMeetingDTO getMeetingDetail(Long meetingId, String email) throws IdInvalidException {
        Meeting meeting = this.meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IdInvalidException("Meeting không tồn tại"));

        String mentorEmail = meeting.getSlot().getMentor().getUser().getEmail();
        String menteeEmail = meeting.getInvoice().getMentee().getUser().getEmail();
        if (!email.equals(menteeEmail) && !email.equals(mentorEmail)) {
            throw new IdInvalidException("Bạn không có quyền xem meeting này");
        }

        return this.convertToMeetingDTO(meeting);
    }

    public ResMeetingDTO convertToMeetingDTO(Meeting meeting) throws IdInvalidException {
        ResMeetingDTO dto = new ResMeetingDTO();
        dto.setDate(meeting.getDate());
        dto.setEndTime(meeting.getEndTime());
        dto.setStartTime(meeting.getStartTime());
        dto.setLocation(meeting.getLocation());
        dto.setPaymentStatus(meeting.getInvoice().getPaymentStatus());
        // Tìm booking để dẫn tới meeting này
        Booking booking = this.bookingRepository
                .findByPlanRegistrationRegistrationId(meeting.getInvoice().getPlanRegistration().getRegistrationId())
                .orElseThrow(() -> new IdInvalidException("Không tồn tại booking cho meeting này"));
        Plan plan = booking.getPlan();
        // Entity Plan dùng @Inheritance(JOINED). Khi gọi booking.getPlan(),
        // Hibernate sẽ trả về một Proxy của class cha (Plan$HibernateProxy) chứ không
        // phải object thực tế. Do đó, (plan instanceof PlanSession) sẽ luôn bằng false.
        // Dùng Hibernate.unproxy() để "bóc" lớp proxy ra, lấy entity thật bên dưới.
        Plan realPlan = (Plan) org.hibernate.Hibernate.unproxy(plan);

        String planName = "";
        if (realPlan instanceof PlanSession session) {
            planName = session.getSessionName();
        } else if (realPlan instanceof PlanMentorship planMentorship) {
            planName = planMentorship.getPlanName();
        }

        dto.setPlanName(planName);

        dto.setStatus(meeting.getStatus());
        dto.setInvoiceId(meeting.getInvoice().getInvoiceId());
        dto.setMeetingId(meeting.getMeetingId());
        dto.setBookingId(booking.getBookingId());
        dto.setMenteeName(
                booking.getMentee().getUser().getFirstName() + " " + booking.getMentee().getUser().getLastName());
        dto.setMentorName(meeting.getSlot().getMentor().getUser().getFirstName() + " "
                + meeting.getSlot().getMentor().getUser().getLastName());

        return dto;
    }

}
