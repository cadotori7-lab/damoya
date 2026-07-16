package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.MemberVO;

public interface MemberMapper {
    
    void registerMember(MemberVO member); //회원가입
    MemberVO findByLoginId(String login_id); // 로그인 ID로 회원 조회
    List<MemberVO> findAllMembers(); // 모든 회원 조회
}