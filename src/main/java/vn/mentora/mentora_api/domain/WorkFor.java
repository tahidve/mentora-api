package vn.mentora.mentora_api.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "work_for")
@Getter
@Setter
public class WorkFor {

    // Composite PK gồm mentorId + companyId
    @EmbeddedId
    private WorkForId id;

    private boolean isCurrent;

    @ManyToOne(fetch = FetchType.LAZY)
    // @MapsId("mentorId"): khi setMentor(mentor) → tự động id.mentorId =
    // mentor.getId()
    @MapsId("mentorId")
    @JoinColumn(name = "mentor_id")
    @JsonIgnore
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    // @MapsId("companyId"): khi setCompany(company) → tự động id.companyId =
    // company.getId()
    @MapsId("companyId")
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_title_id")
    private JobTitle jobTitle;

    private LocalDate startDate;

    @Nullable
    private LocalDate endDate;
}
