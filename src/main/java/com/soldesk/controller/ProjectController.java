package com.soldesk.controller;

import com.soldesk.service.ProjectService;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk.vo.ProjectVO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/project")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;


    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 프로젝트 목록 페이지
    @GetMapping("/list")
    public String list(Model model) {
        
        List<ProjectVO> projectList = projectService.getAllProjects();

        model.addAttribute("projectList", projectList);

        return "project/list";
    }

    // 프로젝트 상세 페이지 
    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long projectId, Model model) {
        ProjectVO project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        return "project/detail";
    }

    // 프로젝트 등록 폼 
    @GetMapping("/form")
    public String form() {
        return "project/form";
    }    

    // 프로젝트 등록
    @PostMapping("/register")
    public String registerProject(@ModelAttribute ProjectVO projectVO, HttpSession session) { 
        projectVO.setOwnerId(1L); // 임시 오너 ID 설정
        projectService.registerProject(projectVO);
        return "redirect:/project/list";
    }
    
    //수정 페이지
    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long projectId,
                            Model model){
        ProjectVO project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("mode", "update");//수정 모드로
        return "project/form";
    }
    
    //수정 처리
    @PostMapping("/update")
    public String updateForm(@ModelAttribute ProjectVO projectVO){
        projectService.updateProject(projectVO);
        return "redirect:/project/detail?id=" + projectVO.getProjectId();
    }

    //삭제 처리 
    @GetMapping("/delete")
    public String deleteProject(@RequestParam("id") Long projectId) {
        projectService.deleteProject(projectId);
        return "redirect:/project/list";
    }
    
    

    @GetMapping("/my")
    public String my() {
        return "project/my";
    }
}
