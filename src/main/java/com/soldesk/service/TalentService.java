package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.TalentMapper;
import com.soldesk.vo.TalentVO;

@Service
public class TalentService {

    private final CommentService commentService;
    @Autowired 
    private TalentMapper talentMapper;

    TalentService(CommentService commentService) {
        this.commentService = commentService;
    }

    // 인재풀 등록
    @Transactional
    public void registerTalent(TalentVO talent, Long memberId){
        talent.setMemberId(memberId);
        talentMapper.insertTalent(talent);
    }

    // 필터 조건이 반영된 전체 데이터 개수 조회
    @Transactional
    public int getTotalCount(TalentVO vo) {
        return talentMapper.getTotalCount(vo);
    }

    //  필터 및 페이징이 반영된 리스트 조회
    @Transactional
    public List<TalentVO> getTalentList(TalentVO vo) {
        return talentMapper.getTalentList(vo);
    }

    // 상세 페이지 조회
    @Transactional
    public TalentVO getTalentById(Long postId){
        return talentMapper.getTalentById(postId);
    }

    // 인재풀 게시글 수정
    @Transactional
    public void updateTalent(TalentVO talentVO){
        talentMapper.updateTalent(talentVO);
    }

    // 인재풀 게시글 삭제
    @Transactional
    public void deleteTalent(Long postId){
        // commentService.deleteCommentsByProjectId(postId); 이건 나중에
        talentMapper.deleteTalent(postId);
    }
    

}