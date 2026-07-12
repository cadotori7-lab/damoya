package com.soldesk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.vo.MemberVO;

@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 PasswordEncoder 주입

    @Transactional
    public void registerMember(MemberVO member) { //회원가입

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberMapper.registerMember(member);
    }

    public MemberVO findByLoginId(String login_id) {
        return memberMapper.findByLoginId(login_id);
    }
}
