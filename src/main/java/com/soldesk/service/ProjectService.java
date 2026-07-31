// 프로젝트 찾기 
package com.soldesk.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.mapper.ProjectMapper;
import com.soldesk.vo.ParticipationVO;
import com.soldesk.vo.ProjectVO;

@Service
public class ProjectService {
    
    @Autowired  
    private ProjectMapper projectmapper;

    @Autowired
    private ParticipationMapper participationMapper;

    @Autowired
    private CommentService commentService;


    //프로젝트 등록
    @Transactional
    public void registerProject(ProjectVO project, Long memberId){
        
        projectmapper.insertProject(project);

        ParticipationVO leaderVO = new ParticipationVO();
        leaderVO.setProjectId(project.getProjectId());
        leaderVO.setMemberId(memberId);

        //  참여 테이블에 리더로 INSERT
        participationMapper.insertProjectLeader(leaderVO);
    }

    @Transactional
    public List<ProjectVO> getAllProjects(){
        return projectmapper.getAllProjects();
    }

    //등록된 프로젝트 가져오기
    @Transactional
    public ProjectVO getProjectById(Long projectId){
        return projectmapper.getProjectById(projectId);
    }

    //프로젝트 모집글 수정
    @Transactional
    public void updateProject(ProjectVO projectVO){
        projectmapper.updateProject(projectVO);
    }

    //프로젝트 모집글 삭제
    @Transactional 
    public void deleteProject(Long projectId){
        // 1. 해당 프로젝트의 댓글 삭제
        commentService.deleteCommentsByProjectId(projectId);
        projectmapper.deleteProject(projectId);
    }

    // 조건에 맞는 프로젝트 리스트 가져오기
    @Transactional
    public List<ProjectVO> getProjectList(ProjectVO vo){
        return projectmapper.getProjectList(vo);
    }

    // 조건에 맞는 프로젝트 개수 가져오기
    @Transactional
    public int getTotalCount(ProjectVO vo){
        return projectmapper.getTotalCount(vo);
    }
    

    // 관심 등록 및 취소
    @Transactional
    public boolean toggleFavorite(Long memberId, Long projectId){
        //좋아요 눌렀는지 확인
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("projectId", projectId);

        int count = projectmapper.checkFavorite(params);

        if(count > 0){
            //이미 눌렀으면 삭제 후 감소
            projectmapper.deleteFavorite(params);
            projectmapper.decreaseFavoriteCount(projectId);
            return false; //좋아요 취소
        } else{
            // 안눌렀으면 등록 후 증가
            projectmapper.insertFavorite(params);
            projectmapper.increaseFavoriteCount(projectId);
            return true; //좋아요 누름 
        }
    }

    // 사용자가 해당 프로젝트에 좋아요를 눌렀는지 확인하는 메서드
    @Transactional
    public int checkFavorite(Map<String, Object> params) {
        return projectmapper.checkFavorite(params);
    }

    //관심 등록한 게시글 불러오기
    @Transactional
    public List<ProjectVO> getFavoriteProjects(Long memberId){
        return projectmapper.getFavoriteProjectsByMemberId(memberId);
    }
    
    // 게시글 조회수 불러오기
    @Transactional
    public void increaseViewCount(Long projectId){
        projectmapper.increaseViewCount(projectId);
    }
    // 추천순 게시글 3개 불러오기
    @Transactional
    public List<ProjectVO> getTop3RecommendedProjects() {
        return projectmapper.getTop3RecommendedProjects();
    }
}
