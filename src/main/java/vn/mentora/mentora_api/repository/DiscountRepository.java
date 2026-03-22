package vn.mentora.mentora_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    // Lấy discount đang active
    List<Discount> findByActiveTrue();

}
