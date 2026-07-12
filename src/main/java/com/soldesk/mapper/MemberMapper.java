package com.soldesk.mapper;

import com.soldesk.vo.MemberVO;

public interface MemberMapper {
    
    void registerMember(MemberVO member); //회원가입
    MemberVO findByLoginId(String login_id); // 로그인 ID로 회원 조회
}
