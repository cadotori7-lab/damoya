package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.ProjectVO;

public interface ProjectMapper {
    
    void insertProject(ProjectVO project); //프로젝트 등록
    List<ProjectVO> getAllProjects(); //등록된 모든 프로젝트 불러오기
    ProjectVO getProjectById(Long projectId); //
    void updateProject(ProjectVO projectVO); // 프로젝트 모집글 수정 
    void deleteProject(Long projectId); //프로젝트 모집글 삭제

}
