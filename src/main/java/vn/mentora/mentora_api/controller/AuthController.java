package vn.mentora.mentora_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.mentora.mentora_api.domain.request.ReqRegisterMenteeDTO;
import vn.mentora.mentora_api.domain.request.ReqVerifyOtpDTO;
import vn.mentora.mentora_api.service.AuthService;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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

}
