package vn.mentora.mentora_api.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.PaymentStatusEnum;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false)
    private PlanRegistration planRegistration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private Mentee mentee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusEnum paymentStatus = PaymentStatusEnum.PENDING;

    private String currency = "VND";

    @Column(precision = 12, scale = 2)
    private BigDecimal amountSubtotal;

    @Column(precision = 12, scale = 2)
    private BigDecimal amountTotal;

    private Instant paidTime; // null cho đến khi PAID

    @Column(columnDefinition = "TEXT")
    private String note;

    private Instant createdAt;

    private String paymentMethod = "DIRECT";

    @PrePersist
    public void handleCreate() {
        this.createdAt = Instant.now();
    }

}