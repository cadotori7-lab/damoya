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


    @Transactional
    public void registerProject(ProjectVO project){
        
        projectmapper.insertProject(project);
    }

    @Transactional
    public List<ProjectVO> getAllProjects(){
        return projectmapper.getAllProjects();
    }

    @Transactional
    public ProjectVO getProjectById(Long projectId){
        return projectmapper.getProjectById(projectId);
    }

    
}
