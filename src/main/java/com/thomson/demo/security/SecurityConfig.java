package com.thomson.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/tickets").hasAnyRole("CUSTOMER", "AGENT", "ADMIN")
                .requestMatchers("/api/tickets/customer/**").hasRole("CUSTOMER")
                .requestMatchers("/api/tickets/agent/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers("/api/tickets/*/assign/**").hasRole("ADMIN")
                
                .requestMatchers("/api/auth/assign-role").hasRole("ADMIN")
                .requestMatchers("/api/tickets/*/status").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers("/api/tickets/breached").hasRole("ADMIN")
                .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")
                .requestMatchers("/api/sla-policies", "/api/sla-policies/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
