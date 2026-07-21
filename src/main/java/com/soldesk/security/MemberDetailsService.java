package com.soldesk.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.vo.MemberVO;

@Service
public class MemberDetailsService implements UserDetailsService {
    //UserDetailsService 인터페이스는 Spring Security에서 사용자 인증을 처리하기 위해 사용되는 인터페이스입니다.

    @Autowired
    private MemberMapper memberMapper; // 사용자 정보를 DB에서 가져오기 위한 Mapper 주입
    
    @Override
    public UserDetails loadUserByUsername(String login_id) throws UsernameNotFoundException {
        
        MemberVO member = memberMapper.findByLoginId(login_id); // DB에서 로그인 ID로 사용자 정보 조회
        if(member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + login_id); // 사용자가 존재하지 않을 경우 예외 발생
        } // login_id와 일치하는 사용자가 DB에 존재하지 않을 경우 예외 발생
         // UserDetails 객체 생성 및 반환
        if ("WITHDRAWN".equals(member.getAccount_status())) {
            throw new UsernameNotFoundException("탈퇴한 계정입니다.");
        }
        return User.builder()
            .username(member.getLogin_id()) // 사용자 이름 설정 (로그인 ID 사용)
            .password(member.getPassword()) // 암호화된 비밀번호 설정
            .roles(resolveRole(member)) // 사용자 역할 설정
            .build(); // 인증된 사용자 객체 반환
    }
    // 권한설정(단순 문자열을 권한으로 변환)
    private String resolveRole(MemberVO member){
        if("ADMIN".equalsIgnoreCase(member.getRole())){
            return "ADMIN";
        } else {
            if (memberMapper.selectMentorById(member.getMember_id()) > 0) {
                return "MENTOR";
            }
            return "USER";
        }
    }

}
