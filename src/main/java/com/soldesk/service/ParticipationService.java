package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.ParticipationMapper;
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
}