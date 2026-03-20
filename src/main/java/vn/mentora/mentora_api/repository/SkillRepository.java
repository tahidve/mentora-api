package vn.mentora.mentora_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import vn.mentora.mentora_api.domain.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {

    List<Skill> findBySkillIdIn(List<Long> ids);

    boolean existsBySkillName(String skillName);

    Optional<Skill> findBySkillName(String name);

    // Trả về Object[] gồm [Skill, Long mentorCount]
    @Query("SELECT s, COUNT(DISTINCT m) FROM Skill s JOIN s.mentors m GROUP BY s")
    List<Object[]> findAllWithMentorCount();

}
