package vn.mentora.mentora_api.domain.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResAdminStatsDTO {

    private long totalMentees;
    private long totalMentors;
    private long totalMeetings;
    private BigDecimal totalRevenue; // SUM(amountTotal) WHERE paymentStatus = PAID
}
