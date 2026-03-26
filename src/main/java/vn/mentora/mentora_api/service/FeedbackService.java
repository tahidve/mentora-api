package vn.mentora.mentora_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.mentora.mentora_api.domain.Booking;
import vn.mentora.mentora_api.domain.Feedback;
import vn.mentora.mentora_api.domain.Invoice;
import vn.mentora.mentora_api.domain.Meeting;
import vn.mentora.mentora_api.domain.Mentee;
import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.PlanRegistration;
import vn.mentora.mentora_api.domain.request.ReqSubmitFeedbackDTO;
import vn.mentora.mentora_api.domain.response.ResFeedbackDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
import vn.mentora.mentora_api.repository.BookingRepository;
import vn.mentora.mentora_api.repository.FeedbackRepository;
import vn.mentora.mentora_api.repository.MeetingRepository;
import vn.mentora.mentora_api.repository.MentorRepository;
import vn.mentora.mentora_api.util.constant.MeetingStatusEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final MeetingRepository meetingRepository;
    private final MentorRepository mentorRepository;
    private final BookingRepository bookingRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
            MeetingRepository meetingRepository,
            MentorRepository mentorRepository,
            BookingRepository bookingRepository) {
        this.feedbackRepository = feedbackRepository;
        this.meetingRepository = meetingRepository;
        this.mentorRepository = mentorRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ResFeedbackDTO submitFeedback(String email, ReqSubmitFeedbackDTO dto)
            throws IdInvalidException {

        // Lấy Meeting
        Meeting meeting = this.meetingRepository.findById(dto.getMeetingId())
                .orElseThrow(() -> new IdInvalidException("Meeting không tồn tại"));

        if (meeting.getStatus() != MeetingStatusEnum.COMPLETED) {
            throw new IdInvalidException("Chỉ được đánh giá buổi học đã hoàn thành");
        }

        Invoice invoice = meeting.getInvoice();
        Mentee mentee = invoice.getMentee();

        if (!mentee.getUser().getEmail().equals(email)) {
            throw new IdInvalidException("Bạn không tham gia buổi học này");
        }

        // check đã review chưa
        PlanRegistration planRegistration = invoice.getPlanRegistration();

        if (this.feedbackRepository.existsByPlanRegistrationRegistrationId(
                planRegistration.getRegistrationId())) {
            throw new IdInvalidException("Bạn đã đánh giá chương trình này rồi");
        }

        // Lấy Mentor
        Mentor mentor = meeting.getSlot().getMentor();

        // Save Feedback
        Feedback feedback = new Feedback();
        feedback.setPlanRegistration(planRegistration);
        feedback.setStars(dto.getStars());
        feedback.setContent(dto.getContent());
        feedback = this.feedbackRepository.save(feedback);

        // update Mentor rating
        mentor.setTotalStars(mentor.getTotalStars() + dto.getStars());
        mentor.setTotalReviews(mentor.getTotalReviews() + 1);
        this.mentorRepository.save(mentor);

        return this.convertToDTO(feedback, mentee);
    }

    public ResultPaginationDTO getFeedbacksByMentorId(Long mentorId, Pageable pageable) {
        Page<Feedback> page = this.feedbackRepository.findByMentorId(mentorId, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        result.setMeta(meta);

        List<ResFeedbackDTO> dtos = new ArrayList<>();
        for (Feedback f : page.getContent()) {
            Booking booking = this.bookingRepository
                    .findByPlanRegistrationRegistrationId(
                            f.getPlanRegistration().getRegistrationId())
                    .orElse(null);
            Mentee mentee = (booking != null) ? booking.getMentee() : null;
            dtos.add(this.convertToDTO(f, mentee));
        }

        result.setResult(dtos);
        return result;
    }

    private ResFeedbackDTO convertToDTO(Feedback feedback, Mentee mentee) {
        ResFeedbackDTO dto = new ResFeedbackDTO();
        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setStars(feedback.getStars());
        dto.setContent(feedback.getContent());
        dto.setSentTime(feedback.getSentTime());
        if (mentee != null) {
            dto.setMenteeName(mentee.getUser().getFirstName() + " " + mentee.getUser().getLastName());
            dto.setMenteeAvatarUrl(mentee.getUser().getAvatarUrl());
        }
        return dto;
    }
}
