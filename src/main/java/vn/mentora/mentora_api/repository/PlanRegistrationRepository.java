package vn.mentora.mentora_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.mentora.mentora_api.domain.PlanRegistration;

public interface PlanRegistrationRepository extends JpaRepository<PlanRegistration, Long> {
}
