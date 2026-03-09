package org.project.ttnecommerce.config;

import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // AUTH ENDPOINTS
                        .requestMatchers("/auth/customer/login").permitAll()
                        .requestMatchers("/auth/seller/login").permitAll()
                        .requestMatchers("/auth/admin/login").permitAll()
                        .requestMatchers("/auth/logout").permitAll()
                        .requestMatchers("/auth/refresh").permitAll()

                        // CUSTOMER PUBLIC APIS
                        .requestMatchers("/api/customers/register").permitAll()
                        .requestMatchers("/api/customers/activate-customer").permitAll()
                        .requestMatchers("/api/customers/resend-activation-link").permitAll()

                        // PASSWORD RESET
                        .requestMatchers("/api/forgot-password").permitAll()
                        .requestMatchers("/api/reset-password").permitAll()

                        // SELLER REGISTER
                        .requestMatchers("/api/sellers/register").permitAll()

                        // ROLE BASED ACCESS
                        .requestMatchers("/api/customers/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/sellers/**").hasRole("SELLER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ANY OTHER REQUEST
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}