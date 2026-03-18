package vn.mentora.mentora_api.domain.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.PlanStatusEnum;

@Getter
@Setter
public class ResPlanSessionDTO {

    private Long planId;

    private String sessionName;

    private String description;

    private BigDecimal charge;

    private int sessionsDuration;

    private PlanStatusEnum status;


}
