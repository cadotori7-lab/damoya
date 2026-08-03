package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.mapper.MentorMapper;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorSignupVO;
import com.soldesk.vo.PageBean;
import com.soldesk.vo.PublicProfileVO;

@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private MentorMapper mentorMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${pagination.size}")
    private int paginationSize;

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
        mentorMapper.insertMemberForMentor(form); 
        mentorMapper.insertMentor(form);
    }
    // 회원 정보 업데이트
    @Transactional
    public void updateMember(MemberVO member) {
        memberMapper.updateMember(member);
    }
    /**
     * 회원 탈퇴.
     * 상태 변경 + 팀장 승계 + 익명화를 한 트랜잭션으로.
     * 중간에 실패하면 전부 롤백되어 '반쯤 탈퇴된' 상태를 방지한다.
     */
    @Transactional
    public void withdraw(String loginId) {
        MemberVO member = memberMapper.findByLoginId(loginId);
        int memberId = member.getMember_id();
        // (1) 내가 '팀장(LEADER)'으로 있고 아직 진행 중인 프로젝트들
        /* 
        List<Long> ledProjects = participationMapper.findLeaderProjectIds(memberId);
 
        // (2) 각 프로젝트에서 팀장 승계
        for (Long projectId : ledProjects) {
            // 승계 순위(succession_order) 가 가장 앞선 팀원 1명
            Long heir = participationMapper.findNextLeader(projectId, memberId);
 
            if (heir != null) {
                // 후계자를 팀장으로 승격
                participationMapper.updateProjectRole(projectId, heir, "LEADER");
                // project.owner_id 도 갱신 (owner 는 RESTRICT 라 반드시 살아있는 회원이어야 함)
                participationMapper.updateProjectOwner(projectId, heir);
            } else {
                // 남은 팀원이 없으면 프로젝트를 종료 처리(정책에 따라)
                participationMapper.closeProject(projectId);
            }
            // 탈퇴자의 해당 프로젝트 참여는 팀장 자리에서 내려놓음
            participationMapper.updateProjectRole(projectId, memberId, "MEMBER");
        }
        */
        // (3) 개인정보 익명화 (선택) — 이름/이메일 마스킹, 로그인ID는 재사용 방지 위해 유지
        memberMapper.anonymize(memberId);
 
        // (4) 계정 상태를 탈퇴로
        memberMapper.updateAccountStatus(memberId, "WITHDRAWN");
    }
    @Transactional
    public void updatePassword(int member_id, String password) {
        memberMapper.updatePassword(member_id, password);
    }

    /**
     * 비밀번호 변경. 규칙 위반 시 PasswordChangeException을 던지며,
     * 메시지를 그대로 화면에 보여줄 수 있다.
     */
    @Transactional
    public void changePassword(String loginId, String currentPassword,
                                String newPassword, String newPasswordConfirm) {
        MemberVO member = memberMapper.findByLoginId(loginId);

        // 소셜 로그인 계정은 비밀번호 변경 대상이 아님 (provider 기본값이 'LOCAL')
        if (member == null || !"LOCAL".equals(member.getProvider())) {
            throw new PasswordChangeException("소셜 로그인 계정은 비밀번호를 변경할 수 없어요.");
        }
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new PasswordChangeException("현재 비밀번호가 일치하지 않아요.");
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new PasswordChangeException("새 비밀번호가 서로 일치하지 않아요.");
        }
        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            throw new PasswordChangeException("현재와 다른 비밀번호를 입력해주세요.");
        }
        if (newPassword.length() < 8) {
            throw new PasswordChangeException("비밀번호는 8자 이상이어야 해요.");
        }

        updatePassword(member.getMember_id(), passwordEncoder.encode(newPassword));
    }

    // id로 회원 정보 조회
    @Transactional
    public MemberVO getMemberById(Long memberId) {
        return memberMapper.getMemberById(memberId);
    }
    // 관리자 계정 목록 조회
    @Transactional(readOnly = true)
    public List<MemberVO> getAllMembers(int page, String search, String status, String role) {
        int offset = Math.max(page - 1, 0) * paginationSize;
        List<MemberVO> members = memberMapper.getAllMembers(search, status, role, paginationSize, offset);
        for (MemberVO member : members) {
            member.setPassword(null);
            member.setPassword_confirm(null);
            // mentor 테이블에 있으면 화면/필터 기준으로 MENTOR 역할로 표시
            if (!"ADMIN".equalsIgnoreCase(member.getRole())
                    && mentorMapper.selectMentorById(member.getMember_id()) > 0) {
                member.setRole("MENTOR");
            }
        }
        return members;
    }

    // 관리자 계정 목록 페이징
    @Transactional(readOnly = true)
    public PageBean getPageBean(int page, String search, String status, String role) {
        int memberCount = memberMapper.getMemberCount(search, status, role);
        return new PageBean(page, memberCount, paginationSize);
    }

    public PublicProfileVO findPublicProfile(int memberId) {
        PublicProfileVO p = memberMapper.selectPublicProfile(memberId);
        if (p == null) return null;
 
        // 비공개 회원이면 상세 필드를 서버에서 제거(널링)해 최소 정보만 남긴다.
        // 프론트가 아니라 여기서 지우는 것이 핵심 — 응답 자체에 안 담긴다.
        if (!p.isPublic()) {
            p.setIntro(null);
            p.setField(null);
            p.setCareer(null);
            p.setMajor(null);
        }
        return p;
    }

}
