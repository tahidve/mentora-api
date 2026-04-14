package vn.mentora.mentora_api.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.GenderEnum;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;

@Getter
@Setter
@NoArgsConstructor
public class ResAdminMenteeDTO {

    private Long menteeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String country;
    private GenderEnum gender;
    private UserStatusEnum status;
    private String goal;
    private Instant createdAt;
    private long totalBookings;
}
