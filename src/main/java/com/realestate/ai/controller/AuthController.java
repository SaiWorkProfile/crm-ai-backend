package com.realestate.ai.controller;

import com.realestate.ai.dto.LoginRequest;
import com.realestate.ai.dto.ResetPasswordRequest;
import com.realestate.ai.dto.SetPasswordRequest;
import com.realestate.ai.model.Admin;
import com.realestate.ai.model.ClientUser;
import com.realestate.ai.model.Partner;
import com.realestate.ai.model.PartnerActivationToken;
import com.realestate.ai.repository.ClientUserRepository;
import com.realestate.ai.repository.PartnerActivationTokenRepository;
import com.realestate.ai.repository.PartnerRepository;
import com.realestate.ai.security.JwtUtil;
import com.realestate.ai.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final PartnerActivationTokenRepository tokenRepo;
    private final PartnerRepository partnerRepo;
    private final ClientUserRepository clientRepo;

    public AuthController(
            AuthService authService,
            JwtUtil jwtUtil,
            PartnerActivationTokenRepository tokenRepo,
            PartnerRepository partnerRepo,
            ClientUserRepository clientRepo
    ) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.tokenRepo = tokenRepo;
        this.partnerRepo = partnerRepo;
        this.clientRepo = clientRepo;
    }

    // ================= ADMIN LOGIN =================//
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest requestBody,
                                     HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String device = request.getHeader("User-Agent");

        Admin admin = authService.login(
                requestBody.getEmail(),
                requestBody.getPassword(),
                ip,
                device
        );

        String jwt = jwtUtil.generateToken(admin.getEmail(), admin.getRole().name());

        return Map.of(
                "token", jwt,
                "role", admin.getRole().name()
        );
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal Admin admin,
                                    HttpServletRequest request) {

        authService.logout(
                admin,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        return ResponseEntity.ok("Logged out successfully");
    }

    // ============ LOGOUT ALL DEVICES ============
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(@AuthenticationPrincipal Admin admin,
                                       HttpServletRequest request) {

        authService.logoutAll(
                admin,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        return ResponseEntity.ok("Logged out from all devices");
    }

    // =========== FORGOT PASSWORD ===========
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        authService.sendResetOtp(body.get("email"));
        return ResponseEntity.ok("OTP sent");
    }

    // =========== RESET PASSWORD ===========
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest body,
                                           HttpServletRequest request) {

        authService.resetPassword(
                body.getEmail(),
                body.getOtp(),
                body.getNewPassword(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        return ResponseEntity.ok("Password reset successful");
    }

    // 🔥 PARTNER SET PASSWORD (ACTIVATION LINK)
    @PostMapping("/client/set-password")
    public ResponseEntity<?> setPassword(
            @RequestBody SetPasswordRequest req){

        PartnerActivationToken t =
                tokenRepo.findByToken(req.getToken())
                        .orElseThrow(() ->
                                new RuntimeException("Invalid Token"));

        if(t.getExpiry().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token Expired");
        }

        Partner partner =
                partnerRepo.findById(t.getPartnerId())
                        .orElseThrow(() ->
                                new RuntimeException("Partner Not Found"));

        ClientUser user =
        		clientRepo.findFirstByEmail(partner.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException("User Not Found"));

        user.setPassword(req.getPassword());
        clientRepo.save(user);

        return ResponseEntity.ok("Password Set Successfully");
    }
}
