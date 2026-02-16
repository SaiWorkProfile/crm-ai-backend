package com.realestate.ai.service;
import com.realestate.ai.model.PasswordResetOtp;
import com.realestate.ai.model.Admin;
import com.realestate.ai.repository.PasswordResetOtpRepository;
import com.realestate.ai.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PasswordResetService {

    private final PasswordResetOtpRepository otpRepo;
    private final AdminRepository userRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            PasswordResetOtpRepository otpRepo,
            AdminRepository userRepo,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.otpRepo = otpRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void sendOtp(String email) {
        Admin user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        otpRepo.deleteByEmail(email);

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        PasswordResetOtp entity = new PasswordResetOtp();
        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setExpiry(LocalDateTime.now().plusMinutes(5));

        otpRepo.save(entity);
        emailService.sendOtp(email, otp);
    }

    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        PasswordResetOtp record = otpRepo.findByEmailAndOtp(email, otp)
                .filter(o -> o.getExpiry().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        Admin user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        otpRepo.deleteByEmail(email);
    }
}
