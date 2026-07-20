package com.soldesk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorSignupVO;

@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerMember(MemberVO member) { //회원가입

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberMapper.registerMember(member);
    }
    @Transactional
    public MemberVO findByLoginId(String login_id) {
        return memberMapper.findByLoginId(login_id);
    }
    // 로그인 ID 중복 확인
    @Transactional
    public int countByLoginId(String loginId) {
        return memberMapper.countByLoginId(loginId);
    }
    // 이메일 중복 확인
    @Transactional
    public int countByEmail(String email) {
        return memberMapper.countByEmail(email);
    }
    // 멘토 회원가입 (member + mentor 테이블)
    @Transactional
    public void registerMentor(MentorSignupVO form) { 
        form.setPassword(passwordEncoder.encode(form.getPassword()));
        memberMapper.insertMemberForMentor(form); 
        memberMapper.insertMentor(form);
    }
}
