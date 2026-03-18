package vn.mentora.mentora_api.domain.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateSessionDTO {
    @NotBlank(message = "Tên session không được để trống")
    private String sessionName;

    private String description;

    @NotNull(message = "Giá không được để trống")
    private BigDecimal charge;

    @Min(value = 15, message = "Tối thiểu 15 phút")
    @Max(value = 120, message = "Tối đa 120 phút")
    private int sessionsDuration = 30;
}
