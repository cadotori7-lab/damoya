package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.ParticipationVO;
import com.soldesk.vo.ProjectVO;

@Service
public class ParticipationService {

    @Autowired
    private ParticipationMapper participationMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MemberMapper memberMapper;

    // 프로젝트 지원하기
    @Transactional
    public void applyForProject(ParticipationVO vo){
        ProjectVO project = projectService.getProjectById(vo.getProjectId());
        // 디버깅용: 콘솔에 찍히는지 확인해보세요!
        System.out.println(">>> 프로젝트 ID: " + vo.getProjectId() + ", 회원 ID: " + vo.getMemberId());
        
        int existingCount = participationMapper.countByProjectAndMember(vo.getProjectId(), vo.getMemberId());
        System.out.println(">>> 이미 지원한 내역 개수: " + existingCount);
        
        if(existingCount > 0){
            throw new IllegalStateException("이미 지원했거나 참여 중인 프로젝트입니다.");
        }
        participationMapper.insertApplication(vo);
        if (project != null) {
            notificationService.toMessage(vo.getProjectId(), project.getOwnerId().intValue(), "APPLY", project.getTitle());
        }
    }

    @Transactional
    public List<ParticipationVO> getApplicants(Long projectId){
        return participationMapper.selectApplicationList(projectId);
    }
    @Transactional
    public List<ParticipationVO> getParticipatingProjectsByMemberId(int memberId, int limit){
        return participationMapper.selectParticipationListByMemberId(memberId, limit);
    }
    @Transactional
    public List<ParticipationVO> getApplicationProjectsByMemberId(int memberId, int limit){
        return participationMapper.selectApplicationListByMemberId(memberId, limit);
    }
    @Transactional
    public void cancelApplication(Long projectId, Long memberId){
        participationMapper.deleteApplication(projectId, memberId);
    }
    @Transactional
    public List<ParticipationVO> getDoneParticipatingProjectsByMemberId(int memberId, int limit){
        return participationMapper.selectDoneParticipationListByMemberId(memberId, limit);
    }
        // 업무 담당자로 선택 가능한 참여자 목록
    @Transactional(readOnly = true)
    public List<ParticipationVO> selectTaskMembers(long project_id) {
        return participationMapper.selectTaskMembers(project_id);
    }
    // 해당 사용자가 프로젝트 팀장인지 확인
    @Transactional(readOnly = true)
    public boolean isLeader(long project_id, long member_id) {
        return participationMapper.countLeader(
                project_id,
                member_id
        ) > 0;
    }
    // 해당 사용자가 업무 담당자로 선택 가능한지 확인
    @Transactional(readOnly = true)
    public boolean isTaskMember(long project_id, long member_id) {
        return participationMapper.countTaskMember(
                project_id,
                member_id
        ) > 0;
    }

    @Transactional(readOnly = true)
    public String getProjectRole(long projectId, long memberId) {
        return participationMapper.selectJoinedProjectRole(projectId, memberId);
    }

    @Transactional(readOnly = true)
    public boolean canReadProject(long projectId, long memberId) {
        return getProjectRole(projectId, memberId) != null;
    }
    // 제의받은 프로젝트 리스트
    @Transactional(readOnly = true)
    public List<ParticipationVO> getOfferedProjectsByMemberId(int memberId, int limit){
        return participationMapper.selectOfferedProjectsByMemberId(memberId, limit);
    }
    // 제의받은 프로젝트 수락
    @Transactional
    public void acceptOfferedProject(long projectId, long memberId){
        int nextOrder = participationMapper.selectNextSuccessionOrder(projectId);
        int ownerId = projectService.getProjectById(projectId).getOwnerId().intValue();
        MemberVO member = memberMapper.getMemberById(memberId);
        notificationService.toMessage(projectId, ownerId, "OFFER_ACCEPTED", "제안 수락: " + projectService.getProjectById(projectId).getTitle() + " (" + member.getName() + ") 님이 제안을 수락했습니다.");
        if (participationMapper.acceptOfferedProject(projectId, memberId, nextOrder) != 1) {
            throw new IllegalStateException("수락할 수 있는 제의를 찾을 수 없습니다.");
        }
    }

    // 제의받은 프로젝트 거절
    @Transactional
    public void rejectOfferedProject(long projectId, long memberId){
        if (participationMapper.rejectOfferedProject(projectId, memberId) != 1) {
            throw new IllegalStateException("거절할 수 있는 제의를 찾을 수 없습니다.");
        }
    }

    // AI 추천 멘토에게 참여 제안 (프로젝트 팀장만 가능)
    @Transactional
    public void offerMentor(long projectId, long actorMemberId, long mentorMemberId){
        if (!isLeader(projectId, actorMemberId)) {
            throw new AccessDeniedException("프로젝트 팀장만 멘토를 제안할 수 있습니다.");
        }
        if (participationMapper.countByProjectAndMember(projectId, mentorMemberId) > 0) {
            throw new IllegalStateException("이미 참여했거나 제안한 멘토입니다.");
        }
        notificationService.toMessage(projectId, (int) mentorMemberId, "OFFER_RECEIVED", "멘토 제안: " + projectService.getProjectById(projectId).getTitle());
        participationMapper.insertMentorOffer(projectId, mentorMemberId);
    }
}
