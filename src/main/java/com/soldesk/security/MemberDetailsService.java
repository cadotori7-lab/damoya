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

    @Autowired
    private MemberMapper memberMapper; 
    
    @Override
    public UserDetails loadUserByUsername(String login_id) throws UsernameNotFoundException {
        
        MemberVO member = memberMapper.findByLoginId(login_id); 
        if(member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + login_id); 
        } 
        if ("WITHDRAWN".equals(member.getAccount_status())) {
            throw new UsernameNotFoundException("탈퇴한 계정입니다.");
        }
        return User.builder()
            .username(member.getLogin_id()) // 사용자 이름 설정 (로그인 ID 사용)
            .password(member.getPassword()) // 암호화된 비밀번호 설정
            .roles(resolveRole(member)) // 사용자 역할 설정
            .build(); // 인증된 사용자 객체 반환
    }
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
