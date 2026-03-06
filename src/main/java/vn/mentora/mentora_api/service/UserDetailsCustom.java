package vn.mentora.mentora_api.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import vn.mentora.mentora_api.repository.UserRepository;

import java.util.Collections;

@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsCustom(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        vn.mentora.mentora_api.domain.User user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority(user.getRole().name())));
    }
}
