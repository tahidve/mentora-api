package vn.mentora.mentora_api.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// Trong thư mục domain/request/
@Getter
@Setter
public class ReqCompanyDTO {

    @NotBlank(message = "Tên công ty không được để trống")
    private String companyName;

}
