package com.soldesk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.ParticipationVO;

public interface ParticipationMapper {

    // 업무 담당자로 선택할 수 있는 참여자 목록
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
}