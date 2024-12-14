package com.sesac.backend.auths.utils;

import static com.sesac.backend.auths.constants.SecurityConstants.*;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .securityContext(context -> context
                .requireExplicitSave(false)  // 비동기 처리시 SecurityContext 자동 전파
            )
            .authorizeHttpRequests(auth ->
                auth
                    .requestMatchers(AUTHS_API, HEALTH_CHECK_URL).permitAll()
                    .requestMatchers("GET", "/courses").permitAll()
                    .requestMatchers("GET", "/courses/{courseId}").permitAll()
                    .requestMatchers("/payments/webhook").permitAll()
                    .requestMatchers("GET", "/courses/{courseId}/reviews").permitAll()
                    .requestMatchers("GET", "/courses/{courseId}/scores").permitAll()
                    .requestMatchers("GET", "/reviews/{reviewId}/likes").permitAll()
                    .requestMatchers(STUDENTS_API).hasRole("STUDENT")
                    .requestMatchers(INSTRUCTORS_API).hasRole("INSTRUCTOR")
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));  // 모든 출처 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);  // '*' 사용시 allowCredentials는 false여야 함

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}