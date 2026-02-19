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

    // OTP MAIL (Already using)
    public void sendOtp(String to, String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom("Manortha Admin <saiwweram@gmail.com>");
        msg.setSubject("Manortha Admin Password Reset OTP");
        msg.setText(
            "Your OTP is: " + otp +
            "\n\nThis OTP is valid for 5 minutes." +
            "\n\nIf you did not request this, ignore this email."
        );

        mailSender.send(msg);
    }

    // 🔥 NEW METHOD FOR PARTNER ACTIVATION
    public void sendActivationLink(String to, String link){

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom("Manortha Admin <saiwweram@gmail.com>");
        msg.setSubject("Activate Your Partner Account");
        msg.setText(
            "Welcome Partner,\n\n" +
            "Click below link to set your password:\n\n" +
            link +
            "\n\nLink valid for 24 hours."
        );

        mailSender.send(msg);
    }
}
