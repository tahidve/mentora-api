package vn.mentora.mentora_api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
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

}
