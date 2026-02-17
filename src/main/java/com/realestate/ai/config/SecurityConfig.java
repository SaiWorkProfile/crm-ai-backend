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
            // 🔥 VERY IMPORTANT FOR JWT (TWILIO BROKE THIS EARLIER)
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())

            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

            	    // ================= PREFLIGHT =================
            	    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

            	    // ================= PUBLIC =================
            	    .requestMatchers("/api/auth/**").permitAll()
            	    .requestMatchers("/api/voice/**").permitAll()
            	    .requestMatchers("/api/ai/**").permitAll()
            	    .requestMatchers("/api/webhook/twilio/**").permitAll()

            	    // ================= DASHBOARD =================
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

            	    // ================= LEADS =================
            	    .requestMatchers(HttpMethod.GET,"/api/admin/leads/**")
            	    .hasAnyRole(
            	        "SUPER_ADMIN",
            	        "ADMIN",
            	        "SALES_MANAGER",
            	        "MARKETING_MANAGER",
            	        "FINANCE",
            	        "SUPPORT",
            	        "VIEW_ONLY"
            	    )

            	    .requestMatchers(HttpMethod.POST,"/api/admin/leads/**")
            	    .hasAnyRole(
            	        "SUPER_ADMIN",
            	        "ADMIN",
            	        "SALES_MANAGER",
            	        "MARKETING_MANAGER"
            	    )

            	    .requestMatchers(HttpMethod.PUT,"/api/admin/leads/**")
            	    .hasAnyRole(
            	        "SUPER_ADMIN",
            	        "ADMIN",
            	        "SALES_MANAGER",
            	        "SUPPORT"
            	    )

            	    // ================= DEALS =================
            	    .requestMatchers(HttpMethod.GET,"/api/admin/deals/**")
            	    .hasAnyRole(
            	        "SUPER_ADMIN",
            	        "ADMIN",
            	        "SALES_MANAGER",
            	        "FINANCE"
            	    )

            	    .requestMatchers(HttpMethod.POST,"/api/admin/deals/**")
            	    .hasAnyRole(
            	        "SUPER_ADMIN",
            	        "ADMIN",
            	        "SALES_MANAGER"
            	    )

            	    .requestMatchers(HttpMethod.PUT,"/api/admin/deals/**")
            	    .hasAnyRole(
            	        "SUPER_ADMIN",
            	        "ADMIN",
            	        "FINANCE"
            	    )

            	    // ================= PROJECTS =================
            	    .requestMatchers("/api/admin/projects/**")
            	    .hasAnyRole(
            	        "SUPER_ADMIN",
            	        "ADMIN"
            	    )

            	    // ================= ROLE MODULES =================
            	    .requestMatchers("/api/superadmin/**")
            	    .hasRole("SUPER_ADMIN")

            	    .requestMatchers("/api/sales/**")
            	    .hasRole("SALES_MANAGER")

            	    .requestMatchers("/api/marketing/**")
            	    .hasRole("MARKETING_MANAGER")

            	    .requestMatchers("/api/finance/**")
            	    .hasRole("FINANCE")

            	    .requestMatchers("/api/support/**")
            	    .hasRole("SUPPORT")

            	    .requestMatchers("/api/view/**")
            	    .hasRole("VIEW_ONLY")

            	    // 🔥 KEEP THIS LAST
            	    .requestMatchers("/api/admin/**")
            	    .hasAnyRole("SUPER_ADMIN","ADMIN")

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

        config.setAllowedMethods(List.of(
            "GET","POST","PUT","DELETE","OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

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
