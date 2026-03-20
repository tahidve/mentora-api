package vn.mentora.mentora_api.domain.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResAvailableFilterDTO {
    List<String> countries;
    List<String> languages;
    List<SkillOption> skills;

    @Getter
    @Setter
    public static class SkillOption {
        private Long skillId;
        private String skillName;
        private Long mentorCount;
    }
}
