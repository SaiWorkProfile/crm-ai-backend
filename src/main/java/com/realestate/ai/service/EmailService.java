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

    // ================= OTP MAIL =================
    public void sendOtp(String to, String otp) {

        System.out.println("📧 OTP EMAIL TO: " + to);

        try {

            SimpleMailMessage msg = new SimpleMailMessage();

            msg.setTo(to);
            msg.setFrom("saiwweram@gmail.com"); // ⚠️ ONLY EMAIL (NO NAME)
            msg.setSubject("Manortha CRM Password Reset OTP");

            msg.setText(
                "Your OTP is: " + otp +
                "\n\nValid for 5 minutes." +
                "\n\nIgnore if not requested."
            );

            mailSender.send(msg);

            System.out.println("✅ OTP EMAIL SENT");

        } catch (Exception e) {

            System.out.println("❌ OTP EMAIL FAILED");
            e.printStackTrace();
        }
    }


    // ================= PARTNER ACTIVATION =================
    public void sendActivationLink(String to, String link) {

        System.out.println("📧 ACTIVATION EMAIL TO: " + to);

        try {

            SimpleMailMessage msg = new SimpleMailMessage();

            msg.setTo(to);
            msg.setFrom("saiwweram@gmail.com"); // ⚠️ IMPORTANT
            msg.setSubject("Activate Your Manortha Partner Account");

            msg.setText(
                "Welcome to Manortha CRM\n\n" +
                "Click below link to set your password:\n\n" +
                link +
                "\n\nThis link expires in 24 hours.\n\n" +
                "If you did not request this, ignore this email."
            );

            mailSender.send(msg);

            System.out.println("✅ ACTIVATION EMAIL SENT");

        } catch (Exception e) {

            System.out.println("❌ ACTIVATION EMAIL FAILED");
            e.printStackTrace();
        }
    }
}