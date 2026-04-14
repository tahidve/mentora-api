package vn.mentora.mentora_api.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAdminReviewMentorDTO {

    @NotBlank(message = "action không được để trống (ACCEPT hoặc REJECT)")
    private String action;

    private String reason;
}
