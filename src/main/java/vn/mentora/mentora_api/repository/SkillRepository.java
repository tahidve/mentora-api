package vn.mentora.mentora_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.mentora.mentora_api.domain.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {

    List<Skill> findBySkillIdIn(List<Long> ids);

    boolean existsBySkillName(String skillName);

    Optional<Skill> findBySkillName(String name);

}
