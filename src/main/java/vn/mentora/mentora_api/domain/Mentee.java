package vn.mentora.mentora_api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mentees")
@Getter
@Setter
public class Mentee {

    @Id
    private Long menteeId;

    private String goal;

    @OneToOne
    @MapsId
    @JoinColumn(name = "mentee_id")
    private User user;

}
