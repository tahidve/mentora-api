package vn.mentora.mentora_api.domain.response;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.PaymentStatusEnum;

@Getter
@Setter
@NoArgsConstructor
public class ResAdminInvoiceDTO {

    private Long invoiceId;
    private String menteeName;
    private BigDecimal amountSubtotal;
    private BigDecimal amountTotal;
    private PaymentStatusEnum paymentStatus;
    private String paymentMethod;
    private Instant paidTime;
    private Instant createdAt;
}
