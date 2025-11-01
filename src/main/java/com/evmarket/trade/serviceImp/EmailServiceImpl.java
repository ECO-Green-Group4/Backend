package com.evmarket.trade.serviceImp;

import com.evmarket.trade.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP - EV Trade");
            message.setText(
                "Hello,\n\n" +
                "You have requested to reset your password for your EV Trade account.\n\n" +
                "Your OTP code is: " + otp + "\n\n" +
                "This code is valid for 5 minutes.\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "EV Trade Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendContractOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Contract Signing OTP - EV Trade");
            message.setText(
                "Xin chào,\n\n" +
                "Bạn đang yêu cầu ký hợp đồng trên hệ thống EV Trade.\n\n" +
                "Mã OTP xác nhận ký hợp đồng của bạn là: " + otp + "\n\n" +
                "Mã có hiệu lực trong 5 phút.\n\n" +
                "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "EV Trade Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email: " + e.getMessage());
        }
    }
}

