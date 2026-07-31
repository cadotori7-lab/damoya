package com.soldesk.mapper;

import java.util.List;
import com.soldesk.vo.TalentVO;

public interface TalentMapper {
    void insertTalent(TalentVO talent); // 인재풀 게시글 등록
    int getTotalCount(TalentVO vo); // 인재풀 게시글 전체 개수
    List<TalentVO> getTalentList(TalentVO vo);  //전체 인재풀 게시글 가져오기    
    TalentVO getTalentById(Long postId); // id 값으로 인재풀 게시글 불러오기 
    void updateTalent(TalentVO talentVO); //인재풀 게시글 수정
    void deleteTalent(Long postId); //인재풀 게시글 삭제
}