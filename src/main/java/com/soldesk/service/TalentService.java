package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.mapper.TalentMapper;
import com.soldesk.vo.TalentVO;

@Service
public class TalentService {

    @Autowired
    private final CommentService commentService;

    @Autowired 
    private TalentMapper talentMapper;

    @Autowired
    private ParticipationMapper participationMapper;


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

    // 내가 팀장인 프로젝트 목록 조회
    @Transactional
    public List<TalentVO> getLeaderProjectsByMemberId(Long memberId){
        return talentMapper.getLeaderProjectsByMemberId(memberId);
    }

    // 함께하기 제의 저장
    @Transactional
    public void insertOffer(Long projectId, Long talentId, Long memberId){
        // 제안 데이터를 Map으로 생성
        Map<String,Object> offerData = new HashMap<>();
        offerData.put("projectId", projectId);
        offerData.put("talentId", talentId);
        offerData.put("memberId", memberId);

        // Mapper를 통해 제안 데이터 저장
        talentMapper.insertOffer(offerData);
    }
    


}