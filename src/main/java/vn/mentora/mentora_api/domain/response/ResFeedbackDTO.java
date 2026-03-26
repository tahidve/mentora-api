package vn.mentora.mentora_api.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResFeedbackDTO {

    private Long feedbackId;

    private int stars;

    private String content;

    private Instant sentTime;

    private String menteeName;

    private String menteeAvatarUrl;

}
