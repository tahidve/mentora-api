package vn.mentora.mentora_api.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqVerifyOtpDTO {
    private String email;
    private String otp;
}
