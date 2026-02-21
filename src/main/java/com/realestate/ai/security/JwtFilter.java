package com.realestate.ai.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.realestate.ai.model.Admin;
import com.realestate.ai.repository.AdminRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;

    public JwtFilter(JwtUtil jwtUtil,
                     AdminRepository adminRepository) {
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("====== JWT FILTER START ======");
        System.out.println("REQUEST URI: " + request.getRequestURI());

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            System.out.println("OPTIONS REQUEST - SKIPPING");
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null) {
            System.out.println("❌ NO AUTH HEADER FOUND");
        }

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            System.out.println("TOKEN RECEIVED");

            if (jwtUtil.validateToken(token)) {

                System.out.println("✅ JWT VALID");

                String email = jwtUtil.extractEmail(token);
                String role  = jwtUtil.extractRole(token);

                System.out.println("EMAIL FROM TOKEN: " + email);
                System.out.println("ROLE FROM TOKEN: " + role);

                if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                    Admin admin = adminRepository
                            .findByEmail(email)
                            .orElse(null);

                    System.out.println("ADMIN FROM DB: " + admin);

                    if(admin==null){
                        System.out.println("❌ ADMIN NOT FOUND IN RENDER DB");
                    }
                    else{
                        System.out.println("ADMIN ACTIVE: "+admin.isActive());
                    }

                    if (admin != null && admin.isActive()) {

                        UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                            );

                        SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);

                        System.out.println("🔥 AUTHENTICATION SET SUCCESS");
                    }
                    else{
                        System.out.println("❌ AUTHENTICATION NOT SET");
                    }
                }
            }
            else{
                System.out.println("❌ JWT INVALID");
            }
        }

        System.out.println("====== JWT FILTER END ======");

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/api/auth")
            || path.startsWith("/api/voice")
            || path.startsWith("/api/ai")
            || path.startsWith("/api/webhook");
    }
}