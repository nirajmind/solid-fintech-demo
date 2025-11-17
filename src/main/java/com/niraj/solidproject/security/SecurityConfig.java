package com.niraj.solidproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (not needed for stateless, token-based APIs)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Define authorization rules
                .authorizeHttpRequests(auths -> auths
                        // Allow our login endpoint
                        .requestMatchers("/api/auth/login").permitAll()
                        // Allow the H2 console
                        .requestMatchers("/h2-console/**").permitAll()
                        // Secure all other requests
                        // 3. NEW: Allow Registration (POST /api/users only)
                        // We use HttpMethod.POST so that GET /api/users (list users) is STILL secured
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .anyRequest().authenticated()
                )

                // 3. Set session management to STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Add our custom JWT filter *before* the standard auth filter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

                // 5. Allow H2 console frames (for spring.security.web.header.writers.frame-options)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                );

        return http.build();
    }

    // Bean to expose the AuthenticationManager (used in AuthController)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
