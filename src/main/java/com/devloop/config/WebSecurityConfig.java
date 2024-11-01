package com.devloop.config;

import com.devloop.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtSecurityFilter jwtSecurityFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtSecurityFilter, SecurityContextHolderAwareRequestFilter.class)
                .formLogin(form -> form.disable())
//                .anonymous(anonymous -> anonymous.disable())  // payment 프론트 테스트 위해 익명 접근 가동하도록 주석처리 하겠습니다.
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login",
                                "/api/v1/auth/kakao/login",
                                "/api/v1/main/search/**",
                                "api/v1/pwts/**",
                                // payments test 위해 열어두는 API
                                "/payments/**",
                                "/success.html",
                                "/fail.html",
                                "/success",
                                "/fail",
                                "/error",
                                "/error/**",
                                "/favicon.ico"
                        )
                        .permitAll()
                        .requestMatchers(
                                "/api/v1/tutor/**"
                        )
                        .hasAnyAuthority(UserRole.authority.TUTOR, UserRole.authority.ADMIN)
                        .requestMatchers(
                                "/api/v1/admin/**"
                        )
                        .hasAuthority(UserRole.authority.ADMIN)
                        .anyRequest().authenticated()
                )
                .build();
    }
}