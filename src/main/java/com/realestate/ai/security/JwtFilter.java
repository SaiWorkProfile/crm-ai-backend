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

        if(request.getMethod().equalsIgnoreCase("OPTIONS")){
            filterChain.doFilter(request,response);
            return;
        }

        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            if(jwtUtil.validateToken(token)) {

                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token).replace("ROLE_", "");


                // 🔥🔥🔥 REMOVE ROLE_ PREFIX FROM JWT
                if(role.startsWith("ROLE_")){
                    role = role.substring(5);
                }

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
                                    // 🔥 ADD BACK ONLY ONE ROLE_
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        // ❌ DO NOT FILTER LOGIN OR PUBLIC APIs
        return path.startsWith("/api/auth")
            || path.startsWith("/api/voice")
            || path.startsWith("/api/ai")
            || path.startsWith("/api/webhook");
    }

}
