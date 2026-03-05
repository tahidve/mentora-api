package vn.mentora.mentora_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.mentora.mentora_api.domain.Mentee;

public interface MenteeRepository extends JpaRepository<Mentee, Long>, JpaSpecificationExecutor<Mentee> {
}
