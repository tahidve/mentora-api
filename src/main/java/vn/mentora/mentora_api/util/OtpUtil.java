package vn.mentora.mentora_api.util;

import java.security.SecureRandom;

public class OtpUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public static String generateOtp() {
        int otp = RANDOM.nextInt(900000) + 100000; // Range: 100000-999999
        return String.valueOf(otp);
    }

    public static String generateOtp(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
