package vn.mentora.mentora_api.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.mentora.mentora_api.domain.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByPlanRegistrationRegistrationId(Long registrationId);

    // tổng doanh thu từ invoice đã PAID
    // COALESCE tránh trả null khi chưa có invoice nào PAID
    @Query("SELECT COALESCE(SUM(i.amountTotal), 0) FROM Invoice i WHERE i.paymentStatus = 'PAID'")
    BigDecimal sumTotalRevenue();

    // Admin GET /invoices (paginated)
    Page<Invoice> findAll(Pageable pageable);

    // list invoices theo mentee
    List<Invoice> findByMenteeMenteeId(Long menteeId);
}
