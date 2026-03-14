package vn.mentora.mentora_api.domain.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.ResponseTimeEnum;

@Getter
@Setter
public class ResMentorProfileDTO {
    private Long id;
    private String name;
    private String avatarUrl;
    private String bio;
    private String headline;
    private ResponseTimeEnum responseTime;
    private int totalMentee;
    private int totalStars;
    private int totalReviews;
    private List<String> languages;
    private List<String> skills;
    private List<ResWorkExperienceDTO> workExperiences;
    private List<String> badges;
}
