package vn.mentora.mentora_api.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.MeetingStatusEnum;

@Entity
@Table(name = "meetings")
@Getter
@Setter
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id")
    private Slot slot;

    @Enumerated(EnumType.STRING)
    private MeetingStatusEnum status;

    private String location; // lưu meeting link

    // private String reviewLink;

    private LocalTime startTime; // snapshot từ slot
    private LocalTime endTime; // snapshot từ slot
    private LocalDate date; // snapshot từ slot

}
