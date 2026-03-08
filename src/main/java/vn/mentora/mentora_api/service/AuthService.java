package vn.mentora.mentora_api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.Mentee;
import vn.mentora.mentora_api.domain.User;
import vn.mentora.mentora_api.domain.request.ReqRegisterMenteeDTO;
import vn.mentora.mentora_api.repository.MenteeRepository;
import vn.mentora.mentora_api.repository.UserRepository;
import vn.mentora.mentora_api.util.OtpUtil;
import vn.mentora.mentora_api.util.constant.RoleEnum;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MenteeRepository menteeRepository;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MenteeRepository menteeRepository,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.menteeRepository = menteeRepository;
        this.emailService = emailService;
    }

    public User registerMentee(ReqRegisterMenteeDTO dto) throws IdInvalidException {

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

        // send verify email
        this.emailService.sendOtpEmail(dto.getEmail(), dto.getFirstName(), otp);

        return savedUser;

    }
}
