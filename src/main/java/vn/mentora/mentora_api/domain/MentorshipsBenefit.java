package vn.mentora.mentora_api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mentorships_benefits")
@Getter
@Setter
public class MentorshipsBenefit {

    @EmbeddedId
    private MentorshipsBenefitId id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String benefitContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("planId") // Ánh xạ planId trong BenefitId với ID của PlanMentorship
    @JoinColumn(name = "plan_id")
    private PlanMentorship planMentorship;

}
