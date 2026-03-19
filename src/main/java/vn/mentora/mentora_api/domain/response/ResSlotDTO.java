package vn.mentora.mentora_api.domain.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.SlotStatusEnum;

@Getter
@Setter
public class ResSlotDTO {
    private Long slotId;
    private Long mentorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatusEnum status;
}
