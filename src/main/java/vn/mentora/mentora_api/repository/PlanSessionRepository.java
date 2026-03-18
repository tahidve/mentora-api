package vn.mentora.mentora_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.PlanSession;

@Repository
public interface PlanSessionRepository extends JpaRepository<PlanSession, Long> {
    List<PlanSession> findByMentorMentorId(Long mentorId);
    boolean existsBySessionNameAndMentorMentorId(String sessionName, Long mentorId);
}
