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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    new AntPathRequestMatcher("/admin/**"))
                .hasRole("ADMIN") // /admin/** 경로는 ADMIN 권한을 가진 사용자만 접근 가능
                .requestMatchers(
                    new AntPathRequestMatcher("/feedback/**")
                )
                .hasRole("MENTOR") // /feedback/** 경로는 MENTOR 권한을 가진 사용자만 접근 가능
                .requestMatchers(
                    new AntPathRequestMatcher("/home"),
                    new AntPathRequestMatcher("/project/my"),
                    new AntPathRequestMatcher("/mypage/**"),
                    new AntPathRequestMatcher("/workspace/**"))
                .authenticated()
                .anyRequest().permitAll()) // 위의 경로를 제외한 모든 요청은 인증 없이 접근 허용
            .formLogin(form -> form
                .loginPage("/auth/login") // 커스텀 로그인 페이지 경로 설정
                .loginProcessingUrl("/auth/login") // 로그인 처리 URL 설정
                .usernameParameter("login_id") // 로그인 시 사용할 사용자 이름 파라미터 설정
                .passwordParameter("password") // 로그인 시 사용할 비밀번호 파라미터 설정
                .defaultSuccessUrl("/home", true) // 로그인 성공 시 이동할 URL 설정
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
