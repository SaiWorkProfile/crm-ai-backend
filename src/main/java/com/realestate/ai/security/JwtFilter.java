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
import com.realestate.ai.repository.ClientUserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepo;
    private final ClientUserRepository clientRepo;

    public JwtFilter(
        JwtUtil jwtUtil,
        AdminRepository adminRepo,
        ClientUserRepository clientRepo){

        this.jwtUtil = jwtUtil;
        this.adminRepo = adminRepo;
        this.clientRepo = clientRepo;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")){

            String token = header.substring(7);

            if(jwtUtil.validateToken(token)){

                String email = jwtUtil.extractEmail(token);
                String role  = jwtUtil.extractRole(token);

                if(SecurityContextHolder
                        .getContext()
                        .getAuthentication()==null){

                    // 🔥 ADMIN LOGIN
                    if(role.equals("ROLE_ADMIN")
                    || role.equals("ROLE_SUPER_ADMIN")){

                        adminRepo.findByEmail(email)
                        .ifPresent(admin -> {

                            UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                admin,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                            );

                            SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                        });
                    }

                    // 🔥 CLIENT / PARTNER LOGIN
                    else if(role.equals("ROLE_PARTNER")
                    || role.equals("ROLE_CLIENT")){

                        clientRepo.findFirstByEmail(email)
                        .ifPresent(user -> {

                            UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                            );

                            SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                        });
                    }
                }
            }
        }

        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
    	
    	   if(request.getMethod().equalsIgnoreCase("OPTIONS")){
    	        return true;
    	    }

        String path = request.getServletPath();

        return path.startsWith("/api/auth")
            || path.startsWith("/api/client/login")
            || path.startsWith("/api/voice")
            || path.startsWith("/api/ai")
            || path.startsWith("/api/webhook");
    }
}