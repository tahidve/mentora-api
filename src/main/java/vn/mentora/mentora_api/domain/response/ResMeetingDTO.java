package vn.mentora.mentora_api.domain.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.MeetingStatusEnum;
import vn.mentora.mentora_api.util.constant.PaymentStatusEnum;

@Getter
@Setter
public class ResMeetingDTO {

    private Long meetingId;

    private MeetingStatusEnum status;

    private String location;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDate date;

    private String mentorName;

    private String menteeName;

    private String planName;

    private Long invoiceId;

    private PaymentStatusEnum paymentStatus;

}
