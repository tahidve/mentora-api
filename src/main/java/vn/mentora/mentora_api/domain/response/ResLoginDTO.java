package vn.mentora.mentora_api.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.RoleEnum;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;

@Getter
@Setter
public class ResLoginDTO {

    private String accessToken;

    private UserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private RoleEnum role;
        private UserStatusEnum status;
        private String avatarUrl;
    }

}
