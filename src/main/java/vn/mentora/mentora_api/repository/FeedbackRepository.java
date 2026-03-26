package vn.mentora.mentora_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Check xem đã review chưa
    boolean existsByPlanRegistrationRegistrationId(Long registrationId);

    // list feedbacks
    Page<Feedback> findByPlanRegistrationPlanMentorMentorId(Long mentorId, Pageable pageable);

}
