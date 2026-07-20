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

@Configuration // 스프링 시큐리티 설정을 위한 구성 클래스, 스프링 설정 파일임을 명시
@EnableWebSecurity // 스프링 시큐리티를 활성화
public class SecurityConfig {

    @Autowired
    private MemberDetailsService memberDetailsService; // 사용자 인증을 위한 MemberDetailsService 주입

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
                    new AntPathRequestMatcher("/project/**"),
                    new AntPathRequestMatcher("/talent/**"),
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
            .authenticationProvider(authenticationProvider) // 사용자 인증을 위한 DaoAuthenticationProvider 설정
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error/403") // 권한 없는 접근 시 이 URL로 리다이렉트
            );
        return http.build();
        
    } //사용자의 요청을 단계적으로 어떤 보안 필터를 적용할 지 결정하는 객체
    //1) .authenticated() : 인증된 사용자만 접근 가능
    // .authorizeHttpRequests() : 사용자의 요청에 대한 접근 권한을 설정
    // .anyRequest().permitAll() : 위에서 지정하지 않은 모든 요청에 대해 접근 허용
    //2) formLogin() : 로그인 페이지 관련 설정
    //3) logout() : 로그아웃 관련 설정
    //4) authenticationProvider() : 사용자 인증을 위한 DaoAuthenticationProvider 설정

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 생성
    }// 비밀번호 암호화를 위한 PasswordEncoder 빈 생성

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(memberDetailsService); // 사용자 정보를 가져오는 UserDetailsService 설정
        authProvider.setPasswordEncoder(passwordEncoder); // 비밀번호 암호화 방식 설정
        return authProvider;
    } // 사용자 인증을 처리하는 객체
   
}
