package vn.mentora.mentora_api.domain.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateMentorshipDTO {

    @NotBlank(message = "Tên plan không được để trống")
    private String planName;

    private String description;

    @NotNull(message = "Giá không được để trống")
    private BigDecimal charge;

    @Min(value = 1, message = "Số call tối thiểu là 1")
    private int callsPerMonth = 4;

    @Min(value = 15, message = "Tối thiểu 15 phút/call")
    @Max(value = 120, message = "Tối đa 120 phút/call")
    private int minutesPerCall = 30;

    private List<String> benefits = new ArrayList<>();
}
