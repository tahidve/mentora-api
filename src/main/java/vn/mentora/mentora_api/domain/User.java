package vn.mentora.mentora_api.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.util.constant.GenderEnum;
import vn.mentora.mentora_api.util.constant.RoleEnum;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String firstName;
    private String lastName;

    @Column(unique = true) // thiết lập ràng buộc duy nhất
    private String email;

    private String password;
    private String phone;
    private String avatarUrl;
    private String country;
    private String timezone;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Enumerated(EnumType.STRING)
    private UserStatusEnum status;

    private String otp;
    private Instant otpExpiration;

    @Column(columnDefinition = "TEXT") // JWT token dài ~500+ ký tự, vượt VARCHAR(255)
    private String refreshToken;

    // Phân biệt "email đã verify" khỏi "account status"
    // MENTEE: verify OTP → emailVerified=true + status=ACTIVE
    // MENTOR: verify OTP → emailVerified=true + status=PENDING (chờ Admin)
    private boolean emailVerified = false;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleUpdate() {
        this.updatedAt = Instant.now();
    }

}
