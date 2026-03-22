package vn.mentora.mentora_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.mentora.mentora_api.domain.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByPlanRegistrationRegistrationId(Long registrationId);

}
