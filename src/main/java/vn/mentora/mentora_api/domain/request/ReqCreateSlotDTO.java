package vn.mentora.mentora_api.domain.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateSlotDTO {

    @NotNull(message = "Ngày bắt đầu không được để trống")
    LocalDate date;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    LocalTime startTime;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    LocalTime endTime;

}
