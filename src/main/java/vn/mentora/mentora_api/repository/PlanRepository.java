package vn.mentora.mentora_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.Plan;
import vn.mentora.mentora_api.util.constant.PlanStatusEnum;
import vn.mentora.mentora_api.util.constant.PlanTypeEnum;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByMentorMentorIdAndStatus(Long mentorId, PlanStatusEnum status);

    List<Plan> findByMentorMentorId(Long mentorId);

    // Lấy plans theo loại (SESSION / MENTORSHIP) của 1 mentor cụ thể
    List<Plan> findByPlanTypeAndMentorMentorId(PlanTypeEnum planType, Long mentorId);
}
