package vn.mentora.mentora_api.domain.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRegisterMentorDTO {

    @NotBlank(message = "First name không được để trống")
    private String firstName;

    @NotBlank(message = "Last name không được để trống")
    private String lastName;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Headline không được để trống!")
    private String headline;

    @NotBlank(message = "Bio không được để trống!")
    private String bio;

    private List<Long> skillIds;

}
