package com.soldesk.mapper;

import java.util.List;
import java.util.Map;

import com.soldesk.vo.ProjectVO;

public interface ProjectMapper {
    
    void insertProject(ProjectVO project); //프로젝트 등록
    List<ProjectVO> getAllProjects(); //등록된 모든 프로젝트 불러오기
    ProjectVO getProjectById(Long projectId); //
    void updateProject(ProjectVO projectVO); // 프로젝트 모집글 수정 
    void deleteProject(Long projectId); //프로젝트 모집글 삭제
    int getTotalCount(ProjectVO projectVO);//필터 조건에 맞는 전체 게시글 개수 조회
    List<ProjectVO> getProjectList(ProjectVO projectVO); // 필터 조건에 맞는 게시글 목록 조회
    void insertFavorite(Map<String, Object> params); // 관심 등록 추가
    void deleteFavorite(Map<String, Object> params); // 관심 등록 취소
    void increaseFavoriteCount(Long projectId);     // 프로젝트 좋아요 수 증가 (+)
    void decreaseFavoriteCount(Long projectId);     // 프로젝트 좋아요 수 감소 (-)
    int checkFavorite(Map<String, Object> params);  // 이미 좋아요를 눌렀는지 확인
    List<ProjectVO> getFavoriteProjectsByMemberId(Long memberId);
    void increaseViewCount(Long projectId); //게시글 조회수 반환
    long countByStatus(String status); // 상태별 프로젝트 수

}
