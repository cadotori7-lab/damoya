package com.soldesk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorDocument;
import com.soldesk.vo.PublicProfileVO;

public interface MemberMapper {

    List<MemberVO> getAllMembers(@Param("search") String search,
                                 @Param("status") String status,
                                 @Param("role") String role,
                                 @Param("amount") int amount,
                                 @Param("offset") int offset); // 관리자 계정 목록 조회

    void registerMember(MemberVO member); //회원가입
    MemberVO findByLoginId(String login_id); // 로그인 ID로 회원 조회
    int countByLoginId(String loginId); // 로그인 ID 중복 확인
    int countByEmail(String email); // 이메일 중복 확인
    MemberVO selectMemberById(int member_id); // 회원 고유번호로 회원 조회

    List<MemberVO> findAllMembers(); // 모든 회원 조회
    List<MentorDocument> findAllMentors(); // 멘토 인덱싱용 회원 + 멘토 정보 조회
    MemberVO getMemberById(Long memberId); //회원 정보 가져오기
    long countAllMembers(); // 전체 회원 수

    void updateMember(MemberVO member); // 회원 정보 업데이트
    void updateAccountStatus(@Param("memberId") int memberId, @Param("status") String status); // 회원 계정 상태 업데이트
    void anonymize(int memberId); // 회원 정보 익명화
    void updatePassword(@Param("member_id") int member_id, @Param("password") String password); // 회원 비밀번호 업데이트

    // 구글 OAuth2 로그인 관련 메서드
    MemberVO findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);
    MemberVO findByEmail(@Param("email") String email);
    void insertOAuthMember(MemberVO member);

    //프로필 조회용
    PublicProfileVO selectPublicProfile(@Param("memberId") int memberId);
    List<MemberVO> getApprovedRequiredMembers(); // 승인이 필요한 회원 조회

    void approveMember(int memberId); // 학교 인증 승인
    void rejectMember(int memberId); // 학교 인증 반려
    int countApprovedRequiredMembers(); // 승인이 필요한 회원 수 조회
    int getMemberCount(@Param("search") String search,
                       @Param("status") String status,
                       @Param("role") String role); // 관리자 계정 목록 건수

}
