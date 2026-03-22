package vn.mentora.mentora_api.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateBookingDTO {

    @NotNull(message = "Plan id không được để trống")
    private Long planId;

    @NotNull(message = "Slot id không được để trống")
    private Long slotId;

    private Long discountId;

    private String message;

}
