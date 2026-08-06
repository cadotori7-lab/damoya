package com.soldesk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.ParticipationVO;

public interface ParticipationMapper {
    
    void insertApplication(ParticipationVO vo); //프로젝트 지원
    List<ParticipationVO> selectApplicationList(Long projectId); //지원서 목록
    void insertProjectLeader(ParticipationVO vo); //프로젝트 등록 시 포지션을 리더로 정함
    int countByProjectAndMember(@Param("projectId") Long projectId, @Param("memberId") Long memberId);//이미 지원/참여 중인지 확인하기 위한 카운트 조회
    List<ParticipationVO> selectParticipationListByMemberId(@Param("memberId") int memberId, @Param("limit") int limit); //참여중인 프로젝트 목록 조회
    List<ParticipationVO> selectApplicationListByMemberId(@Param("memberId") int memberId, @Param("limit") int limit); //지원 진행 중인 프로젝트 목록 조회
    List<ParticipationVO> selectDoneParticipationListByMemberId(@Param("memberId") int memberId, @Param("limit") int limit); //참여 완료된 프로젝트 목록 조회
    void deleteApplication(@Param("projectId") Long projectId, @Param("memberId") Long memberId); // 지원 취소
     List<ParticipationVO> selectTaskMembers(
            @Param("project_id") long project_id
    );
    // 해당 사용자가 프로젝트 팀장인지 검사
    int countLeader(
            @Param("project_id") long project_id,
            @Param("member_id") long member_id
    );
    // 해당 사용자가 업무 담당자로 선택 가능한지 검사
    int countTaskMember(
            @Param("project_id") long project_id,
            @Param("member_id") long member_id
    );

    String selectJoinedProjectRole( //역할 조회
            @Param("projectId") long projectId,
            @Param("memberId") long memberId
    );

    List<ParticipationVO> selectJoinedTeamMembers(
            @Param("projectId") long projectId
    );

    List<ParticipationVO> selectWaitingApplicants(
            @Param("projectId") long projectId
    );

    List<ParticipationVO> selectOfferedMembers(
            @Param("projectId") long projectId
    );

    int selectNextSuccessionOrder(@Param("projectId") long projectId);

    int approveWaitingApplicant(
            @Param("projectId") long projectId,
            @Param("memberId") long memberId,
            @Param("successionOrder") int successionOrder
    );

    int rejectWaitingApplicant(
            @Param("projectId") long projectId,
            @Param("memberId") long memberId
    );

    int kickJoinedMember(
            @Param("projectId") long projectId,
            @Param("memberId") long memberId
    );

    List<ParticipationVO> selectSuccessionMembersForUpdate(
            @Param("projectId") long projectId
    );

    int updateSuccessionOrder(
            @Param("projectId") long projectId,
            @Param("memberId") long memberId,
            @Param("successionOrder") int successionOrder
    );

    int leaveJoinedLeader(
            @Param("projectId") long projectId,
            @Param("memberId") long memberId
    );

    int promoteMemberToLeader(
            @Param("projectId") long projectId,
            @Param("memberId") long memberId
    );
    List<ParticipationVO> selectOfferedProjectsByMemberId(
            @Param("memberId") int memberId,
            @Param("limit") int limit
    );
    int acceptOfferedProject(
            @Param("projectId") long projectId,
            @Param("memberId") long memberId,
            @Param("successionOrder") int successionOrder
    );

    int rejectOfferedProject(
            @Param("projectId") long projectId,
            @Param("memberId") long memberId
    );

    void insertMentorOffer(
            @Param("projectId") long projectId,
            @Param("memberId") long memberId
    );
}
