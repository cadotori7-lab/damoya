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
}
