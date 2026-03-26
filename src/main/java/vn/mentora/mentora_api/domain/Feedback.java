package vn.mentora.mentora_api.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    // unique = true → DB enforce 1 PlanRegistration chỉ có tối đa 1 Feedback
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_registration_id", nullable = false, unique = true)
    private PlanRegistration planRegistration;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int stars;

    @Column(columnDefinition = "TEXT")
    private String content; // mentee có thể chỉ chấm sao

    private Instant sentTime;

    @PrePersist
    public void onCreate() {
        this.sentTime = Instant.now();
    }
}
