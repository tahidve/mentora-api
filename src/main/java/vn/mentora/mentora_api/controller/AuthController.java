package vn.mentora.mentora_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.mentora.mentora_api.domain.User;
import vn.mentora.mentora_api.domain.request.ReqLoginDTO;
import vn.mentora.mentora_api.domain.request.ReqRegisterMenteeDTO;
import vn.mentora.mentora_api.domain.request.ReqVerifyOtpDTO;
import vn.mentora.mentora_api.domain.response.ResLoginDTO;
import vn.mentora.mentora_api.service.AuthService;
import vn.mentora.mentora_api.service.UserService;
import vn.mentora.mentora_api.util.SecurityUtil;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final AuthService authService;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(
            AuthService authService,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            UserService userService,
            SecurityUtil securityUtil) {
        this.authService = authService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/register/mentee")
    @ApiMessage("Đăng ký Mentee thành công, kiểm tra email để xác thực!")
    public ResponseEntity<Void> registerMentee(@Valid @RequestBody ReqRegisterMenteeDTO dto) throws IdInvalidException {
        this.authService.registerMentee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/verify-otp")
    @ApiMessage("Xác thực OTP thành công!")
    public ResponseEntity<Void> verifyOtp(@RequestBody ReqVerifyOtpDTO dto) throws IdInvalidException {
        this.authService.verifyOtp(dto);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/login")
    @ApiMessage("Đăng nhập thành công")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO dto) throws IdInvalidException {

        // authenticate
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(),
                dto.getPassword());
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = this.userService.fetchUserByEmail(dto.getEmail());

        if (!user.isEmailVerified()) {
            throw new IdInvalidException("Vui lòng xác thực email trước khi đăng nhập!");
        }
        if (user.getStatus() != UserStatusEnum.ACTIVE) {
            throw new IdInvalidException("Tài khoản chưa được kích hoạt (status: " + user.getStatus() + ")");
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        userLogin.setId(user.getUserId());
        userLogin.setEmail(user.getEmail());
        userLogin.setFirstName(user.getFirstName());
        userLogin.setLastName(user.getLastName());
        userLogin.setRole(user.getRole());
        userLogin.setAvatarUrl(user.getAvatarUrl());
        ResLoginDTO res = new ResLoginDTO();
        res.setUser(userLogin);
        res.setAccessToken(this.securityUtil.createAccessToken(user.getEmail(), user.getRole().name(), userLogin));

        String refreshToken = this.securityUtil.createRefreshToken(user.getEmail(), userLogin);

        this.userService.updateRefreshToken(user.getEmail(), refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true).secure(false).path("/")
                .maxAge(refreshTokenExpiration).sameSite("Lax").build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

}
