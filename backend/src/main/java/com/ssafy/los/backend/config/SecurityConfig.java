package com.ssafy.los.backend.config;

import com.ssafy.los.backend.user.filter.JWTFilter;
import com.ssafy.los.backend.user.filter.LoginFilter;
import com.ssafy.los.backend.user.model.repository.RefreshTokenRepository;
import com.ssafy.los.backend.user.model.service.OAuth2UserService;
import com.ssafy.los.backend.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2UserService oauth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //
        http.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
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
                .requestMatchers("/test/user").hasRole("USER")
                .requestMatchers("/test/admin").hasRole("ADMIN")
                .requestMatchers("/assets/**","/favicon.ico", "/index.html").permitAll()
                .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
//                .anyRequest().authenticated());
                .anyRequest().permitAll());

        // 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);

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

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
