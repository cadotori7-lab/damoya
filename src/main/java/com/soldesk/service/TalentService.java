package com.soldesk.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.CommentMapper;
import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.mapper.TalentMapper;
import com.soldesk.vo.ParticipationVO;
import com.soldesk.vo.ProjectVO;
import com.soldesk.vo.TalentVO;

@Service
public class TalentService {

    @Autowired
    private final CommentService commentService;

    @Autowired 
    private TalentMapper talentMapper;

    @Autowired
    private ParticipationMapper participationMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private NotificationService notificationService;

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
        commentMapper.deleteTalentCommentsByPostId(postId); // 댓글 삭제
        talentMapper.deleteTalent(postId);
    }

    // 내가 팀장인 프로젝트 목록 조회
    @Transactional
    public List<ProjectVO> getLeaderProjectsByMemberId(Long memberId){
        return talentMapper.getLeaderProjectsByMemberId(memberId);
    }

    // 함께하기 제의 저장
    @Transactional
    public void insertOffer(ParticipationVO participationVO){
        talentMapper.insertOffer(participationVO);
        ProjectVO project = projectService.getProjectById(participationVO.getProjectId());
        if (project != null) {
            notificationService.toMessage(
                    participationVO.getProjectId(),
                    participationVO.getMemberId().intValue(),
                    "OFFER_RECEIVED",
                    "인재풀 제안- " + project.getTitle());
        }
    }

    // 특정 인재에게 이미 제의를 보낸 프로젝트 ID 목록 조회
    @Transactional
    public List<Long> getAlreadyOfferedProjectIds(Long memberId) {
        return talentMapper.getAlreadyOfferedProjectIds(memberId);
    }


    // 관심 등록 및 취소
    @Transactional
    public boolean toggleFavorite(Long memberId, Long postId){
        //좋아요 눌렀는지 확인
        Map<String,Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("postId", postId);

        int count = talentMapper.checkFavorite(params);

        if(count > 0){
            //이미 눌렀다면 삭제 후 감소
            talentMapper.deleteFavorite(params);
            talentMapper.decreaseFavoriteCount(postId);
            return false; //좋아요 취소
        }else{
            //안눌렀다면 등록 후 증가
            talentMapper.insertFavorite(params);
            talentMapper.increaseFavoriteCount(postId);
            return true; //좋아요 누름
        }
    }

    // 사용자가 해당 인재글에 좋아요 눌렀는지 확인
    @Transactional
    public int checkFavorite(Map<String, Object> params){
        return talentMapper.checkFavorite(params);
    }

    // 관심 등록한 게시글 불러오기
    @Transactional
    public List<TalentVO> getFavoriteTalents(long memberId) {
    // 만약 기존에 long으로 호출하던 곳이 있다면 TalentVO를 만들어 매퍼로 전달
    TalentVO vo = new TalentVO();
    vo.setMemberId(memberId);
    return talentMapper.getFavoriteTalents(vo);
    }
    public List<TalentVO> getFavoriteTalents(TalentVO vo) {
    // 새로 바꾼 컨트롤러에서 vo 통째로 넘길 때 처리
    return talentMapper.getFavoriteTalents(vo);
}

    // 게시글 조회수 불러오기
    @Transactional
    public void increaseViewCount(Long postId){
        talentMapper.increaseViewCount(postId);
    }
    
    


}