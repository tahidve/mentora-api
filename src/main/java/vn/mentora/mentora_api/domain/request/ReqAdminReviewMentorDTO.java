package vn.mentora.mentora_api.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAdminReviewMentorDTO {

    @NotBlank(message = "Lý do từ chối không được để trống")
    private String reason;
}
