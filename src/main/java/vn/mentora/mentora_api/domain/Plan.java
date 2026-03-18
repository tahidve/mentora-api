package vn.mentora.mentora_api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.PlanStatusEnum;
import vn.mentora.mentora_api.util.constant.PlanTypeEnum;

import java.math.BigDecimal;

@Entity
@Table(name = "plans")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "plan_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal charge;

    @Enumerated(EnumType.STRING)
    private PlanStatusEnum status;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", insertable = false, updatable = false)
    private PlanTypeEnum planType;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

}
