package com.soldesk.mapper;

import java.util.List;
import java.util.Map;

import com.soldesk.vo.TalentVO;

public interface TalentMapper {
    void insertTalent(TalentVO talent); // 인재풀 게시글 등록
    int getTotalCount(TalentVO vo); // 인재풀 게시글 전체 개수
    List<TalentVO> getTalentList(TalentVO vo);  //전체 인재풀 게시글 가져오기    
    TalentVO getTalentById(Long postId); // id 값으로 인재풀 게시글 불러오기 
    void updateTalent(TalentVO talentVO); //인재풀 게시글 수정
    void deleteTalent(Long postId); //인재풀 게시글 삭제
    List<Map<String,Object>> getLeaderProjectsByMemberId(Long memberId); // 로그인한 유저가 팀장인 프로젝트 목록 가져오기
    void insertOffer(Map<String,Object> offerData); // 인재풀 제안 등록
}