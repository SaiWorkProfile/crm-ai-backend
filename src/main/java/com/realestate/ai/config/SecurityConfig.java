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
            // 🔥 VERY IMPORTANT FOR JWT (TWILIO BROKE THIS EARLIER)
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())

            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // 🔥 React preflight fix
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/voice/**").permitAll()
                // ---------- PUBLIC ----------
                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .requestMatchers("/api/admin/projects/**")
                .hasAnyRole("SUPER_ADMIN","ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/webhook/twilio/**").permitAll()
                .requestMatchers("/api/ai/**").permitAll()

                // ---------- DASHBOARD ----------
                .requestMatchers("/api/admin/dashboard")
                .hasAnyRole(
                        "SUPER_ADMIN",
                        "ADMIN",
                        "SALES_MANAGER",
                        "MARKETING_MANAGER",
                        "FINANCE",
                        "SUPPORT",
                        "VIEW_ONLY"
                )

                // ---------- DEALS ----------
                .requestMatchers(HttpMethod.GET, "/api/admin/deals/**")
                .hasAnyRole("SUPER_ADMIN","ADMIN","SALES_MANAGER","FINANCE")

                .requestMatchers(HttpMethod.POST, "/api/admin/deals/**")
                .hasAnyRole("SUPER_ADMIN","ADMIN","SALES_MANAGER","FINANCE")

                // ---------- SUPER ADMIN ----------
                .requestMatchers("/api/superadmin/**")
                .hasRole("SUPER_ADMIN")

                // ---------- ADMIN ----------
                .requestMatchers("/api/admin/**")
                .hasAnyRole("SUPER_ADMIN","ADMIN")

                // ---------- SALES ----------
                .requestMatchers("/api/sales/**")
                .hasRole("SALES_MANAGER")

                // ---------- MARKETING ----------
                .requestMatchers("/api/marketing/**")
                .hasRole("MARKETING_MANAGER")

                // ---------- FINANCE ----------
                .requestMatchers("/api/finance/**")
                .hasRole("FINANCE")

                // ---------- SUPPORT ----------
                .requestMatchers("/api/support/**")
                .hasRole("SUPPORT")

                // ---------- VIEW ----------
                .requestMatchers("/api/view/**")
                .hasRole("VIEW_ONLY")

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ================= CORS =================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
        ));

        config.setAllowedMethods(List.of(
                "GET","POST","PUT","DELETE","OPTIONS"
        ));
        config.setAllowedHeaders(List.of("*"));   // 🔥 VERY IMPORTANT

        config.setExposedHeaders(List.of("Authorization")); // 🔥


        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // ================= PASSWORD =================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ================= AUTH MANAGER =================
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
