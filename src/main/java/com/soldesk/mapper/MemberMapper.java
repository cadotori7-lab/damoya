package com.soldesk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorSignupVO;

public interface MemberMapper {

    void registerMember(MemberVO member); //회원가입
    MemberVO findByLoginId(String login_id); // 로그인 ID로 회원 조회
    int countByLoginId(String loginId); // 로그인 ID 중복 확인
    int countByEmail(String email); // 이메일 중복 확인
    void insertMemberForMentor(MentorSignupVO vo); // 멘토 가입 - member 테이블 INSERT
    void insertMentor(MentorSignupVO vo); // 멘토 가입 - mentor 테이블 INSERT
    MemberVO selectMemberById(int member_id); // 회원 고유번호로 회원 조회
    int selectMentorById(int member_id); // 멘토인지 확인

    List<MemberVO> findAllMembers(); // 모든 회원 조회

    void updateMember(MemberVO member); // 회원 정보 업데이트
    void updateAccountStatus(@Param("memberId") int memberId, @Param("status") String status); // 회원 계정 상태 업데이트
    void anonymize(int memberId); // 회원 정보 익명화

    // 구글 OAuth2 로그인 관련 메서드
    MemberVO findByProviderAndProviderId(String provider, String providerId);
    MemberVO findByEmail(String email);
    void insertOAuthMember(MemberVO member);

}
