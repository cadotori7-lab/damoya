// 프로젝트 찾기 
package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.ProjectMapper;
import com.soldesk.vo.ProjectVO;

@Service
public class ProjectService {
    
    @Autowired  
    private ProjectMapper projectmapper;


    //프로젝트 등록
    @Transactional
    public void registerProject(ProjectVO project){
        
        projectmapper.insertProject(project);
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
        projectmapper.deleteProject(projectId);
    }
    
}
