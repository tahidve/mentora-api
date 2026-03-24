package vn.mentora.mentora_api.domain.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResBookingDTO {

    private Long bookingId;

    private Long invoiceId;

    private Long meetingId;

    private BigDecimal amountSubtotal;

    private BigDecimal amountTotal;

    private String discountApplied;

    private String planName; // tên plan đã đặt

    private String mentorName; // tên mentor

    private LocalDate slotDate; // ngày buổi học

    private LocalTime startTime; // giờ bắt

    private LocalTime endTime; // giờ kết

    private String paymentStatus; // "PENDING"

    private String bookingStatus; // "CONFIRMED"

}
