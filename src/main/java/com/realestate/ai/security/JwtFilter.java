package com.realestate.ai.security;

import com.realestate.ai.model.Admin;
import com.realestate.ai.repository.AdminRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;

    public JwtFilter(JwtUtil jwtUtil,
                     AdminRepository adminRepository) {
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
    }

    // 🔥 SKIP JWT FOR PUBLIC APIs
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI(); // ✅ VERY IMPORTANT CHANGE

        return path.contains("/api/auth")
            || path.contains("/api/voice")
            || path.contains("/api/ai")
            || path.contains("/api/webhook/twilio");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            if(jwtUtil.validateToken(token)) {

                String email = jwtUtil.extractEmail(token);
                String role  = jwtUtil.extractRole(token);
                System.out.println("JWT EMAIL: " + email);
                System.out.println("JWT ROLE: " + role);
                if(email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                    Admin admin = adminRepository
                            .findByEmail(email)
                            .orElse(null);

                    if(admin != null && admin.isActive()) {

                        UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                admin,
                                null,
                                List.of(
                                    new SimpleGrantedAuthority("ROLE_" + role)
                                )
                            );

                        SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                    }
                }
            }
        }

        filterChain.doFilter(request,response);
    }
}
