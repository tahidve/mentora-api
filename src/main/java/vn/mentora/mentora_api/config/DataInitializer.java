package vn.mentora.mentora_api.config;

import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import vn.mentora.mentora_api.domain.User;
import vn.mentora.mentora_api.repository.UserRepository;
import vn.mentora.mentora_api.util.constant.RoleEnum;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Chỉ tạo Admin nếu chưa có trong DB
        if (!userRepository.existsByEmail("admin@mentoria.vn")) {
            User admin = new User();
            admin.setEmail("admin@mentoria.vn");
            admin.setPassword(passwordEncoder.encode("123456")); // Mật khẩu 123456
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setRole(RoleEnum.ADMIN);
            admin.setStatus(UserStatusEnum.ACTIVE);
            admin.setEmailVerified(true);
            admin.setCreatedAt(Instant.now());

            userRepository.save(admin);
            System.out.println(">>> Đã khởi tạo tài khoản ADMIN thành công! (admin@mentoria.vn / 123456)");
        }
    }
}
