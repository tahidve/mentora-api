package vn.mentora.mentora_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.PlanMentorship;

@Repository
public interface PlanMentorshipRepository extends JpaRepository<PlanMentorship, Long> {
    List<PlanMentorship> findByMentorMentorId(Long mentorId);
    boolean existsByPlanNameAndMentorMentorId(String planName, Long mentorId);
}

