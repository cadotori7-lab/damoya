package com.soldesk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.MemberVO;

public interface MemberMapper {

    void registerMember(MemberVO member); //회원가입
    MemberVO findByLoginId(String login_id); // 로그인 ID로 회원 조회
    int countByLoginId(String loginId); // 로그인 ID 중복 확인
    int countByEmail(String email); // 이메일 중복 확인
    MemberVO selectMemberById(int member_id); // 회원 고유번호로 회원 조회

    List<MemberVO> findAllMembers(); // 모든 회원 조회

    void updateMember(MemberVO member); // 회원 정보 업데이트
    void updateAccountStatus(@Param("memberId") int memberId, @Param("status") String status); // 회원 계정 상태 업데이트
    void anonymize(int memberId); // 회원 정보 익명화
    void updatePassword(@Param("member_id") int member_id, @Param("password") String password); // 회원 비밀번호 업데이트

    // 구글 OAuth2 로그인 관련 메서드
    MemberVO findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);
    MemberVO findByEmail(@Param("email") String email);
    void insertOAuthMember(MemberVO member);

}
