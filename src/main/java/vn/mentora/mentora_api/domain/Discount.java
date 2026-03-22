package vn.mentora.mentora_api.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.DiscountTypeEnum;

@Entity
@Table(name = "discounts")
@Getter
@Setter
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @NotBlank(message = "discount name is required")
    private String discountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountTypeEnum discountType;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal discountValue;

    @NotNull(message = "Ngày discount bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày discount kết thúc không được để trống")
    private LocalDate endDate;

    private Instant createdAt;
    private Instant updatedAt;

    private boolean active = true;

    private int usageLimit = 0; // 0 = unlimited
    private int usedCount = 0;

    @PrePersist
    public void handleCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void handleUpdate() {
        this.updatedAt = Instant.now();
    }

}
