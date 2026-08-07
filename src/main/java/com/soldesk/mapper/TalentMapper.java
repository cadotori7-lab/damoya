package com.soldesk.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.ParticipationVO;
import com.soldesk.vo.ProjectVO;
import com.soldesk.vo.TalentVO;

public interface TalentMapper {
    void insertTalent(TalentVO talent); // 인재풀 게시글 등록
    int getTotalCount(TalentVO vo); // 인재풀 게시글 전체 개수
    List<TalentVO> getTalentList(TalentVO vo);  //전체 인재풀 게시글 가져오기    
    TalentVO getTalentById(Long postId); // id 값으로 인재풀 게시글 불러오기 
    void updateTalent(TalentVO talentVO); //인재풀 게시글 수정
    void deleteTalent(Long postId); //인재풀 게시글 삭제
    List<ProjectVO> getLeaderProjectsByMemberId(Long memberId); // 로그인한 회원이 리더로 참여 중인 프로젝트 가져오기
    void insertOffer(ParticipationVO participationVO); // 인재풀 제안 등록
    List<Long> getAlreadyOfferedProjectIds(@Param("memberId") Long memberId); //이미 제의한 프로젝트 ID 찾기
    void insertFavorite(Map<String, Object> params); //관심 등록 추가 
    void deleteFavorite(Map<String, Object> params); //관심 등록 취소 
    void increaseFavoriteCount(Long postId); //프로젝트 좋아요 수 증가
    void decreaseFavoriteCount(Long postId); //프로젝트 좋아요 수 감소
    int checkFavorite(Map<String,Object> params); //이미 좋아요 눌렀는지 확인
    List<TalentVO> getFavoriteTalents(long memberId);
    List<TalentVO> getFavoriteTalents(TalentVO vo);
    void increaseViewCount(Long postId); //게시글 조회수 반환
    


}   