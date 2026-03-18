package vn.mentora.mentora_api.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "plan_mentorships")
@DiscriminatorValue("MENTORSHIP")
@PrimaryKeyJoinColumn(name = "mentorships_id")
@Getter
@Setter
public class PlanMentorship extends Plan {

    private String planName;

    @Min(value = 1)
    private int callsPerMonth = 4;

    @Min(value = 15, message = "Minutes per call minimum 15")
    @Max(value = 120, message = "Minutes per call maximum 120")
    private int minutesPerCall = 30;

    @OneToMany(mappedBy = "planMentorship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MentorshipsBenefit> benefits = new ArrayList<>();

}
