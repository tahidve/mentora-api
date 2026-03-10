package vn.mentora.mentora_api.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateSkillDTO {

    @NotBlank(message = "Skill name is required!")
    private String skillName;

}
