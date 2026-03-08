package vn.mentora.mentora_api.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.RoleEnum;

@Getter
@Setter
public class ResLoginDTO {

    private String accessToken;

    private UserLogin user;

    @Getter
    @Setter
    public static class UserLogin {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private RoleEnum role;
        private String avatarUrl;
    }

}
