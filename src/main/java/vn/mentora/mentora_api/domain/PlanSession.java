package vn.mentora.mentora_api.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "plan_sessions")
@DiscriminatorValue("SESSION")
@PrimaryKeyJoinColumn(name = "sessions_id")
public class PlanSession extends Plan {

    private String sessionName;

    @Max(value = 120, message = "Session duration maximum 120 minutes")
    @Min(value = 15, message = "Session duration minimum 15 minutes")
    private int sessionsDuration = 30;

}
