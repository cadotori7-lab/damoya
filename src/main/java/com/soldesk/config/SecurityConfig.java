package com.soldesk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.soldesk.security.MemberDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MemberDetailsService memberDetailsService; 

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider)
        throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/project/favorite/toggle"),
                    new AntPathRequestMatcher("/notification/read/**"), // fetch()로 CSRF 토큰 없이 보내는 POST 요청
                    new AntPathRequestMatcher("/notification/delete/**"), // 위와 동일
                    new AntPathRequestMatcher("/ws/**")) // SockJS 폴백 전송(xhr-streaming 등)이 보내는 POST 요청은 CSRF 토큰이 없어 별도 예외 처리. 인증은 StompAuthChannelInterceptor가 STOMP CONNECT 단계에서 별도로 검사한다.
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    new AntPathRequestMatcher("/resources/**"))
                .permitAll() // 정적 리소스(CSS/JS/이미지)는 어드민 포함 누구나 항상 접근 가능해야 함
                .requestMatchers(
                    new AntPathRequestMatcher("/admin/**"))
                .hasRole("ADMIN") // /admin/** 경로는 ADMIN 권한을 가진 사용자만 접근 가능
                .requestMatchers(
                    new AntPathRequestMatcher("/feedback/**"),
                    new AntPathRequestMatcher("/mentor/**"))
                .hasRole("MENTOR") // 멘토 전용
                .requestMatchers(
                    new AntPathRequestMatcher("/home"),
                    new AntPathRequestMatcher("/project/my"),
                    new AntPathRequestMatcher("/project/form"),
                    new AntPathRequestMatcher("/talent/form"),
                    new AntPathRequestMatcher("/project/favorite/toggle"),
                    new AntPathRequestMatcher("/mypage/**"),
                    new AntPathRequestMatcher("/workspace/**"))
                .access(new WebExpressionAuthorizationManager("isAuthenticated() and !hasRole('ADMIN')")) // 어드민은 일반 페이지 접근 불가
                .anyRequest().access(new WebExpressionAuthorizationManager("!hasRole('ADMIN')"))) // 어드민이 아니면 그대로 허용(비로그인 포함), 어드민은 /admin/** 외 전부 차단
            .formLogin(form -> form
                .loginPage("/auth/login") // 커스텀 로그인 페이지 경로 설정
                .loginProcessingUrl("/auth/login") // 로그인 처리 URL 설정
                .usernameParameter("login_id") // 로그인 시 사용할 사용자 이름 파라미터 설정
                .passwordParameter("password") // 로그인 시 사용할 비밀번호 파라미터 설정
                .successHandler((request, response, authentication) -> {
                    // 어드민은 일반 페이지 접근이 막혀 있으므로 로그인 후 관리자 페이지로 보낸다
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    response.sendRedirect(request.getContextPath() + (isAdmin ? "/admin/dashboard" : "/home"));
                })
                .failureUrl("/auth/login?error=true") // 로그인 실패 시 이동할 URL 설정
                .permitAll()) // 로그인 페이지는 인증 없이 접근 허용
            .logout(logout -> logout
                .logoutUrl("/auth/logout") // 로그아웃 처리 URL 설정
                .logoutSuccessUrl("/auth/login") // 로그아웃 성공 시 이동할 URL 설정
                .invalidateHttpSession(true) // 로그아웃 시 세션 무효화
                .deleteCookies("JSESSIONID") // 로그아웃 시 쿠키 삭제
                .permitAll()) // 로그아웃은 인증 없이 접근 허용
            .authenticationProvider(authenticationProvider) 
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error/403") // 권한 없는 접근 시 이 URL로 리다이렉트
            );
        return http.build();
        
    } 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(memberDetailsService); 
        authProvider.setPasswordEncoder(passwordEncoder); 
        return authProvider;
    } 
   
}
