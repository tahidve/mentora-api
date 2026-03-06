package vn.mentora.mentora_api.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Slf4j
public class SecurityConfiguration {

    // Dùng HS512 — sau này SecurityUtil sẽ khai báo cùng hằng số này
    private static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${app.jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(getSecretKey())
                .macAlgorithm(JWT_ALGORITHM) // HS512
                .build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                log.error(">>> JWT decode error: ", e);
                throw e;
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            CustomAuthenticationEntryPoint customEntryPoint) throws Exception {

        String[] whiteList = {
                "/"
                // "/test"
        };

        http
                .csrf(c -> c.disable()) // API REST → disable CSRF
                .cors(Customizer.withDefaults()) // Dùng CorsConfig bean
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(whiteList).permitAll() // Public endpoints
                        .anyRequest().authenticated() // Còn lại cần JWT
                )
                .oauth2ResourceServer(oauth2 -> oauth2 // JWT Resource Server
                        .jwt(Customizer.withDefaults()) // Dùng JwtDecoder + Converter beans
                        .authenticationEntryPoint(customEntryPoint) // Custom 401 JSON response
                )
                .formLogin(f -> f.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(this.jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                JWT_ALGORITHM.getName());
    }

}
