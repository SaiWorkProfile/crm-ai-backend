package com.realestate.ai.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);

        // 🔴 VERY IMPORTANT
        msg.setFrom("Manortha Admin <saiwweram@gmail.com>");

        msg.setSubject("Manortha Admin Password Reset OTP");
        msg.setText(
            "Your OTP is: " + otp +
            "\n\nThis OTP is valid for 5 minutes." +
            "\n\nIf you did not request this, ignore this email."
        );

        mailSender.send(msg);
    }
}
