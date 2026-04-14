package vn.mentora.mentora_api.domain.response;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;

@Getter
@Setter
@NoArgsConstructor
public class ResAdminMentorDTO {

    private Long mentorId;
    private String firstName;
    private String lastName;
    private String email;
    private UserStatusEnum status;
    private String headline;
    private String bio;
    private double rating;
    private int totalReviews;
    private int totalMeetings;
    private List<String> skills;
    private Instant createdAt;
    private String rejectionReason;
    private Instant rejectedAt;
}
