package com.ssafy.los.backend.config.security;

import com.ssafy.los.backend.domain.repository.auth.RefreshTokenRepository;
import com.ssafy.los.backend.filter.JWTFilter;
import com.ssafy.los.backend.filter.LoginFilter;
import com.ssafy.los.backend.service.auth.OAuth2UserService;
import com.ssafy.los.backend.service.user.UserStatusService;
import com.ssafy.los.backend.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    private final AuthenticationConfiguration authenticationConfiguration;
    private final OAuth2UserService oauth2UserService;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final UserStatusService userStatusService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //
        http.cors((corsCustomizer -> corsCustomizer.configurationSource(
                new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList(allowedOrigins));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                })));

        // csrf 설정
        http.csrf((auth) -> auth.disable());

        // From 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());

        // http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
//                .requestMatchers("/register").authenticated()
                .requestMatchers("/assets/**", "/favicon.ico", "/index.html").permitAll()
                .requestMatchers("/users", "/oauth2/**", "/login/**").permitAll()
//                .anyRequest().authenticated());
                .anyRequest().permitAll());

        // 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(
                new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,
                        refreshTokenRepository, userStatusService),
                UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // OAuth2 설정
        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) ->
                        userInfoEndpointConfig.userService(oauth2UserService))
                .successHandler(customOAuth2SuccessHandler));

        return http.build();
    }

}
