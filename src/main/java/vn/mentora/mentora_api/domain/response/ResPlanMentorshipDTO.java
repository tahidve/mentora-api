package vn.mentora.mentora_api.domain.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.PlanStatusEnum;

@Getter
@Setter
public class ResPlanMentorshipDTO {

    private Long planId;

    private String planName;

    private String description;

    private BigDecimal charge;

    private int callsPerMonth;

    private int minutesPerCall;

    private PlanStatusEnum status;

    private List<String> benefits;

}
