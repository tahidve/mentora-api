package vn.mentora.mentora_api.domain.response;

import java.util.List;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.ResponseTimeEnum;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;

@Getter
@Setter
public class ResMentorProfileDTO {
    private Long id;
    private String name;
    private UserStatusEnum status;
    private Instant rejectedAt;
    private String rejectionReason;
    private String avatarUrl;
    private String cvUrl;
    private String bio;
    private String headline;
    private ResponseTimeEnum responseTime;
    private int totalMeetings;
    private int totalStars;
    private int totalReviews;
    private List<String> languages;
    private List<String> skills;
    private List<ResWorkExperienceDTO> workExperiences;
    private List<String> badges;
}
