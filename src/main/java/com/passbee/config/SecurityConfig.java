package com.passbee.config;

import com.passbee.auth.jwt.JwtAuthenticationFilter;
import com.passbee.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // HttpMethod import 확인
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// 메서드 수준 보안 활성화를 위한 import
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 메서드 수준 보안 활성화 (@PreAuthorize 사용 위함)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger 경로 허용
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // GET 요청에 대한 공개 경로 설정
                        .requestMatchers(HttpMethod.GET, "/api/licenses/**").permitAll() // 자격증 목록/상세
                        .requestMatchers(HttpMethod.GET, "/api/qna/**").permitAll()      // Q&A 목록/상세
                        .requestMatchers(HttpMethod.GET, "/api/notices/**").permitAll()  // 공지사항 목록/상세
                        // ▼▼▼ [이 줄 추가] 통합 검색(Search) GET 요청도 허용합니다. ▼▼▼
                        .requestMatchers(HttpMethod.GET, "/api/search").permitAll()

                        // 기타 인증 없이 접근 가능한 경로
                        .requestMatchers(
                                "/auth/**", // 회원가입/로그인
                                "/api/qnet/**", // QNet 프록시 API (필요에 따라 검토)
                                "/api/agencies/**", // 기관 정보 (필요에 따라 검토)
                                "/api/stats/**", // 통계 정보 (필요에 따라 검토)
                                "/api/qualifications/**", // 자격 정보 (필요에 따라 검토)
                                "/api/exam-subjects/**", // 시험 과목 정보 (필요에 따라 검토)
                                "/api/admin/**" // 관리자 데이터 수집 API
                        ).permitAll()

                        // 위에서 명시된 경로 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}