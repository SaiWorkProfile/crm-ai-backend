package com.realestate.ai.config;

import com.realestate.ai.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .httpBasic(b -> b.disable())
        .formLogin(f -> f.disable())
        .sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        .authorizeHttpRequests(auth -> auth
        		
        		// ✅ VERY VERY IMPORTANT
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        // PREFLIGHT
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

        // PUBLIC
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/client/login").permitAll()
     // PARTNER APIs
        .requestMatchers("/api/partner/**")
        .hasAuthority("ROLE_PARTNER")
        .requestMatchers("/api/auth/client/set-password").permitAll()
        .requestMatchers("/api/voice/**").permitAll()
        .requestMatchers("/api/ai/**").permitAll()
        .requestMatchers("/api/webhook/twilio/**").permitAll()

        // DASHBOARD
        .requestMatchers("/api/admin/dashboard")
        .hasAnyAuthority(
        "ROLE_SUPER_ADMIN",
        "ROLE_ADMIN",
        "ROLE_SALES_MANAGER",
        "ROLE_MARKETING_MANAGER",
        "ROLE_FINANCE",
        "ROLE_SUPPORT",
        "ROLE_VIEW_ONLY"
        )

        // LEADS
        .requestMatchers(HttpMethod.GET,"/api/admin/leads/**")
        .hasAnyAuthority(
        "ROLE_SUPER_ADMIN",
        "ROLE_ADMIN",
        "ROLE_SALES_MANAGER",
        "ROLE_MARKETING_MANAGER",
        "ROLE_FINANCE",
        "ROLE_SUPPORT",
        "ROLE_VIEW_ONLY"
        )

        .requestMatchers(HttpMethod.POST,"/api/admin/leads/**")
        .hasAnyAuthority(
        "ROLE_SUPER_ADMIN",
        "ROLE_ADMIN",
        "ROLE_SALES_MANAGER",
        "ROLE_MARKETING_MANAGER"
        )

        .requestMatchers(HttpMethod.PUT,"/api/admin/leads/**")
        .hasAnyAuthority(
        "ROLE_SUPER_ADMIN",
        "ROLE_ADMIN",
        "ROLE_SALES_MANAGER",
        "ROLE_SUPPORT"
        )

        // DEALS
        .requestMatchers(HttpMethod.GET,"/api/admin/deals/**")
        .hasAnyAuthority(
        "ROLE_SUPER_ADMIN",
        "ROLE_ADMIN",
        "ROLE_SALES_MANAGER",
        "ROLE_FINANCE"
        )

        .requestMatchers(HttpMethod.POST,"/api/admin/deals/**")
        .hasAnyAuthority(
        "ROLE_SUPER_ADMIN",
        "ROLE_ADMIN",
        "ROLE_SALES_MANAGER"
        )

        .requestMatchers(HttpMethod.PUT,"/api/admin/deals/**")
        .hasAnyAuthority(
        "ROLE_SUPER_ADMIN",
        "ROLE_ADMIN",
        "ROLE_FINANCE"
        )

        .requestMatchers("/api/admin/login-logs")
        .hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")

        // PROJECTS
        .requestMatchers("/api/admin/projects/**")
        .hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")

        // CLIENTS
        .requestMatchers("/api/admin/clients/**")
        .hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")

        // PARTNERS

        .requestMatchers("/api/admin/partners/**")
        .hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")

        // MASTER ADMIN
        .requestMatchers("/api/admin/**")
        .hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")

        .anyRequest().authenticated()
        )

        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
        "http://localhost:5173",
        "http://localhost:3000",
        "https://*.vercel.app"
        ));

        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
        new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(
    AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
