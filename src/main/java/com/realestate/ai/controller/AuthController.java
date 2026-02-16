package com.realestate.ai.controller;
import com.realestate.ai.dto.LoginRequest;
import com.realestate.ai.dto.ResetPasswordRequest;
import com.realestate.ai.model.Admin;
import com.realestate.ai.security.JwtUtil;
import com.realestate.ai.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    // ================= LOGIN =================//
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
}
