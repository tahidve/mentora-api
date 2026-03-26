package vn.mentora.mentora_api.domain.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSubmitFeedbackDTO {

    @NotNull(message = "meetingId không được để trống")
    private Long meetingId;

    @Min(value = 1, message = "Tối thiểu 1 sao")
    @Max(value = 5, message = "Tối đa 5 sao")
    private int stars;

    private String content;

}
