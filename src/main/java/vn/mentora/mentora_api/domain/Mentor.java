package vn.mentora.mentora_api.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.ResponseTimeEnum;

@Entity
@Table(name = "mentors")
@Getter
@Setter
public class Mentor {

    @Id
    private Long mentorId;

    private String bio;
    private String headline;
    private String cvUrl;

    @Enumerated(EnumType.STRING)
    private ResponseTimeEnum responseTime;

    private int totalMentee;
    private int totalStars;
    private int totalReviews;

    @OneToOne
    @MapsId
    @JoinColumn(name = "mentor_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "set_skill", joinColumns = @JoinColumn(name = "mentor_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    @OneToMany(mappedBy = "mentor")
    @JsonIgnore
    private List<WorkFor> workExperiences;
}
