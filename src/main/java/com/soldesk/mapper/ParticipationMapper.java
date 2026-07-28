package com.soldesk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.ParticipationVO;

public interface ParticipationMapper {
    
    void insertApplication(ParticipationVO vo); //프로젝트 지원
    List<ParticipationVO> selectApplicationList(Long projectId); //지원서 목록
    void insertProjectLeader(ParticipationVO vo); //프로젝트 등록 시 포지션을 리더로 정함
    int countByProjectAndMember(@Param("projectId") Long projectId, @Param("memberId") Long memberId);//이미 지원/참여 중인지 확인하기 위한 카운트 조회
    
}
