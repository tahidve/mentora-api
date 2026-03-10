package vn.mentora.mentora_api.domain.request;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqWorkExperienceDTO {
    @NotBlank(message = "Tên công ty không được để trống")
    private String companyName;

    @NotBlank(message = "Chức danh không được để trống")
    private String jobTitleName;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;
}
