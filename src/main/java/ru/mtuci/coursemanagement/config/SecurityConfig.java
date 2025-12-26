package ru.mtuci.coursemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest().authenticated())

                .formLogin(withDefaults())

                .logout(logout -> logout.permitAll())

                .csrf(withDefaults())

                .headers(headers -> headers
                        // Content Security Policy
                        .contentSecurityPolicy(
                                csp -> csp.policyDirectives("default-src 'self'"))

                        // Clickjacking protection
                        .frameOptions(frame -> frame.sameOrigin())

                        // MIME sniffing protection
                        .contentTypeOptions(withDefaults())

                        // Permissions-Policy (Spring Security 5.x)
                        .addHeaderWriter(
                                new org.springframework.security.web.header.writers.StaticHeadersWriter(
                                        "Permissions-Policy",
                                        "geolocation=(), microphone=(), camera=()"))

                        // Referrer-Policy (Spring Security 5.x)
                        .addHeaderWriter(
                                new org.springframework.security.web.header.writers.StaticHeadersWriter(
                                        "Referrer-Policy",
                                        "no-referrer"))

                        // HSTS (использовать только при HTTPS)
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .preload(false)
                                .maxAgeInSeconds(31536000))

                        // COOP / COEP / CORP
                        .addHeaderWriter((req, res) -> res
                                .setHeader("Cross-Origin-Opener-Policy", "same-origin"))
                        .addHeaderWriter((req, res) -> res.setHeader(
                                "Cross-Origin-Embedder-Policy", "require-corp"))
                        .addHeaderWriter((req, res) -> res.setHeader(
                                "Cross-Origin-Resource-Policy", "same-origin")));

        return http.build();
    }
}
