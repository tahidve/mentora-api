package vn.mentora.mentora_api.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
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

    private int totalMeetings;
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

    @ElementCollection
    @CollectionTable(name = "mentor_languages", joinColumns = @JoinColumn(name = "mentor_id"))
    @Column(name = "language")
    private List<String> languages;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL)
    private List<Plan> plans;

    @OneToMany(mappedBy = "mentor")
    private List<Slot> slots;

}
