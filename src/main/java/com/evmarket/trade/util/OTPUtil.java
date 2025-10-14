package com.evmarket.trade.util;

import java.security.SecureRandom;

public class OTPUtil {
    private static final String OTP_CHARS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(OTP_CHARS.charAt(random.nextInt(OTP_CHARS.length())));
        }
        return otp.toString();
    }

    public static boolean verifyOTP(String storedOTP, String inputOTP) {
        return storedOTP != null && storedOTP.equals(inputOTP);
    }
}
