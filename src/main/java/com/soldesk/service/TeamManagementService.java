package com.soldesk.service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.mapper.ProjectMapper;
import com.soldesk.vo.ParticipationVO;

@Service
public class TeamManagementService {

    public static final String LEADER = "LEADER";
    public static final String MENTOR = "MENTOR";

    private final ParticipationMapper participationMapper;
    private final ProjectMapper projectMapper;

    public TeamManagementService(ParticipationMapper participationMapper,
                                 ProjectMapper projectMapper) {
        this.participationMapper = participationMapper;
        this.projectMapper = projectMapper;
    }

    @Transactional(readOnly = true)
    public String getProjectRole(long projectId, long memberId) {
        return participationMapper.selectJoinedProjectRole(projectId, memberId);
    }

    @Transactional(readOnly = true)
    public boolean canViewTeamManagement(long projectId, long memberId) {
        String role = getProjectRole(projectId, memberId);
        return LEADER.equals(role) || MENTOR.equals(role);
    }

    @Transactional(readOnly = true)
    public List<ParticipationVO> getTeamMembers(long projectId, long actorMemberId) {
        requireViewer(projectId, actorMemberId);
        List<ParticipationVO> members = participationMapper
                .selectJoinedTeamMembers(projectId);
        int displayOrder = 1;
        for (ParticipationVO member : members) {
            if ("MEMBER".equals(member.getProjectRole())) {
                member.setSuccessionOrder(displayOrder++);
            }
        }
        return members;
    }

    @Transactional(readOnly = true)
    public List<ParticipationVO> getWaitingApplicants(long projectId, long actorMemberId) {
        requireLeader(projectId, actorMemberId);
        return participationMapper.selectWaitingApplicants(projectId);
    }

    @Transactional
    public void approveApplicant(long projectId, long applicantMemberId, long actorMemberId) {
        requireLeader(projectId, actorMemberId);
        int nextOrder = participationMapper.selectNextSuccessionOrder(projectId);
        if (participationMapper.approveWaitingApplicant(
                projectId, applicantMemberId, nextOrder) != 1) {
            throw new IllegalStateException("대기 중인 지원서를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void rejectApplicant(long projectId, long applicantMemberId, long actorMemberId) {
        requireLeader(projectId, actorMemberId);
        if (participationMapper.rejectWaitingApplicant(projectId, applicantMemberId) != 1) {
            throw new IllegalStateException("대기 중인 지원서를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void kickMember(long projectId, long targetMemberId, long actorMemberId) {
        requireLeader(projectId, actorMemberId);
        if (targetMemberId == actorMemberId) {
            throw new IllegalArgumentException("팀장은 자신을 강퇴할 수 없습니다.");
        }
        if (participationMapper.kickJoinedMember(projectId, targetMemberId) != 1) {
            throw new IllegalStateException("강퇴할 수 있는 팀원을 찾을 수 없습니다.");
        }
        normalizeSuccessionOrders(projectId);
    }

    @Transactional
    public void moveSuccessionOrder(long projectId, long targetMemberId,
                                    String direction, long actorMemberId) {
        requireLeader(projectId, actorMemberId);
        if (!"UP".equals(direction) && !"DOWN".equals(direction)) {
            throw new IllegalArgumentException("올바르지 않은 순위 변경 요청입니다.");
        }

        List<ParticipationVO> members = participationMapper
                .selectSuccessionMembersForUpdate(projectId);
        int currentIndex = -1;
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getMemberId() == targetMemberId) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex < 0) {
            throw new IllegalStateException("순위를 변경할 팀원을 찾을 수 없습니다.");
        }

        int nextIndex = "UP".equals(direction) ? currentIndex - 1 : currentIndex + 1;
        if (nextIndex < 0 || nextIndex >= members.size()) {
            return;
        }
        Collections.swap(members, currentIndex, nextIndex);
        saveSuccessionOrders(projectId, members);
    }

    @Transactional
    public long leaveProjectAsLeader(long projectId, long actorMemberId) {
        requireLeader(projectId, actorMemberId);

        List<ParticipationVO> successionMembers = participationMapper
                .selectSuccessionMembersForUpdate(projectId);
        if (successionMembers.isEmpty()) {
            throw new IllegalStateException(
                    "승계할 팀원이 없어 프로젝트를 나갈 수 없습니다.");
        }

        long successorMemberId = successionMembers.get(0).getMemberId();
        if (participationMapper.leaveJoinedLeader(
                projectId, actorMemberId) != 1) {
            throw new IllegalStateException("팀장 참여 상태를 변경할 수 없습니다.");
        }
        if (participationMapper.promoteMemberToLeader(
                projectId, successorMemberId) != 1) {
            throw new IllegalStateException("승계 대상 팀원을 팀장으로 변경할 수 없습니다.");
        }
        if (projectMapper.updateProjectOwner(projectId, successorMemberId) != 1) {
            throw new IllegalStateException("프로젝트 소유자를 변경할 수 없습니다.");
        }

        successionMembers.remove(0);
        saveSuccessionOrders(projectId, successionMembers);
        return successorMemberId;
    }

    private void normalizeSuccessionOrders(long projectId) {
        saveSuccessionOrders(
                projectId,
                participationMapper.selectSuccessionMembersForUpdate(projectId));
    }

    private void saveSuccessionOrders(long projectId, List<ParticipationVO> members) {
        for (int i = 0; i < members.size(); i++) {
            participationMapper.updateSuccessionOrder(
                    projectId, members.get(i).getMemberId(), i + 1);
        }
    }

    private void requireViewer(long projectId, long memberId) {
        if (!canViewTeamManagement(projectId, memberId)) {
            throw new AccessDeniedException("팀장과 멘토만 팀원 관리를 볼 수 있습니다.");
        }
    }

    private void requireLeader(long projectId, long memberId) {
        if (!LEADER.equals(getProjectRole(projectId, memberId))) {
            throw new AccessDeniedException("프로젝트 팀장만 변경할 수 있습니다.");
        }
    }
}
