package vn.mentora.mentora_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Check xem đã review chưa
    boolean existsByPlanRegistrationRegistrationId(Long registrationId);

    // list feedbacks của 1 Mentor — phải join qua Booking vì PlanRegistration không
    // có FK plan
    // Navigate: Feedback → PlanRegistration ← Booking → Plan → Mentor
    @Query("SELECT f FROM Feedback f " +
            "JOIN Booking b ON b.planRegistration = f.planRegistration " +
            "WHERE b.plan.mentor.mentorId = :mentorId")
    Page<Feedback> findByMentorId(@Param("mentorId") Long mentorId, Pageable pageable);

}
