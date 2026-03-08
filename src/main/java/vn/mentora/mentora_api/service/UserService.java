package vn.mentora.mentora_api.service;

import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.User;
import vn.mentora.mentora_api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(
            UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User fetchUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void updateRefreshToken(String email, String refreshToken) {
        User user = fetchUserByEmail(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
    }

    public User fetchUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return userRepository.findByRefreshTokenAndEmail(refreshToken, email).orElse(null);
    }
}
