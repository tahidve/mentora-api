package vn.mentora.mentora_api.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MentorshipsBenefitId implements Serializable {

    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "benefit_order")
    private Integer benefitOrder;

}
