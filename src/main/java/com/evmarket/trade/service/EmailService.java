package com.evmarket.trade.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendContractOtpEmail(String toEmail, String otp);
}


