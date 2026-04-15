package vn.mentora.mentora_api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.Mentee;
import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.User;
import vn.mentora.mentora_api.domain.request.ReqRegisterMenteeDTO;
import vn.mentora.mentora_api.domain.request.ReqRegisterMentorDTO;
import vn.mentora.mentora_api.domain.request.ReqVerifyOtpDTO;
import vn.mentora.mentora_api.repository.MenteeRepository;
import vn.mentora.mentora_api.repository.MentorRepository;
import vn.mentora.mentora_api.repository.UserRepository;
import vn.mentora.mentora_api.util.OtpUtil;
import vn.mentora.mentora_api.util.constant.RoleEnum;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class AuthService {

    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MenteeRepository menteeRepository;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MenteeRepository menteeRepository,
            EmailService emailService,
            MentorRepository mentorRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.menteeRepository = menteeRepository;
        this.emailService = emailService;
        this.mentorRepository = mentorRepository;
    }

    public void registerMentee(ReqRegisterMenteeDTO dto) throws IdInvalidException {

        if (this.userRepository.existsByEmail(dto.getEmail())) {
            throw new IdInvalidException("Email đã tồn tại!");
        }

        User user = new User();
        user.setRole(RoleEnum.MENTEE);
        user.setStatus(UserStatusEnum.PENDING);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(this.passwordEncoder.encode(dto.getPassword()));

        String otp = OtpUtil.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));

        User savedUser = this.userRepository.save(user);

        // tạo mentee với savedUser thì mới có id để map bằng JPA
        Mentee mentee = new Mentee();
        mentee.setUser(savedUser);
        mentee.setGoal(dto.getGoal());
        this.menteeRepository.save(mentee);

        // TODO: gửi OTP email — implement lại EmailService.sendOtpEmail()
        // this.emailService.sendOtpEmail(dto.getEmail(), dto.getFirstName(), otp);

    }

    public void registerMentor(ReqRegisterMentorDTO dto) throws IdInvalidException {

        if (this.userRepository.existsByEmail(dto.getEmail())) {
            throw new IdInvalidException("Email đã tồn tại!");
        }

        User user = new User();
        user.setRole(RoleEnum.MENTOR);
        user.setStatus(UserStatusEnum.PENDING);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(this.passwordEncoder.encode(dto.getPassword()));

        String otp = OtpUtil.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));

        User savedUser = this.userRepository.save(user);

        Mentor mentor = new Mentor();
        mentor.setBio(dto.getBio());
        // cvUrl làm sau
        // skillids làm sau
        mentor.setHeadline(dto.getHeadline());
        mentor.setUser(savedUser);
        this.mentorRepository.save(mentor);

        // TODO: gửi OTP email — implement lại EmailService.sendOtpEmail() (Phase 16)
        // this.emailService.sendOtpEmail(dto.getEmail(), dto.getFirstName(), otp);
    }

    public User verifyOtp(ReqVerifyOtpDTO dto) throws IdInvalidException {

        User user = this.userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IdInvalidException("Email không tồn tại!"));

        if (user.isEmailVerified()) {
            throw new IdInvalidException("Email đã được xác thực rồi!");
        }

        // Check OTP match
        if (user.getOtp() == null || !user.getOtp().equals(dto.getOtp())) {
            throw new IdInvalidException("Mã OTP không hợp lệ!");
        }

        // Check expiration
        if (user.getOtpExpiration() == null || Instant.now().isAfter(user.getOtpExpiration())) {
            throw new IdInvalidException("Mã OTP đã hết hạn!");
        }

        user.setOtp(null);
        user.setOtpExpiration(null);
        user.setEmailVerified(true); // Đánh dấu email đã verify

        // MENTEE → ACTIVE ngay, MENTOR → giữ PENDING chờ Admin duyệt profile
        if (user.getRole() == RoleEnum.MENTEE) {
            user.setStatus(UserStatusEnum.ACTIVE);
        }

        return this.userRepository.save(user);

    }

}
