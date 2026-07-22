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

    @GetMapping("/list")
    public String list(Model model) {
        
        List<ProjectVO> projectList = projectService.getAllProjects();

        model.addAttribute("projectList", projectList);

        return "project/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long projectId, Model model) {
        ProjectVO project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        return "project/detail";
    }

    
    @GetMapping("/form")
    public String form() {
        return "project/form";
    }    
    @PostMapping("/register")
    public String registerProject(@ModelAttribute ProjectVO projectVO,
                                    HttpSession session
    ) { 
        //     //로그인한 회원ID 가져오기
        //    Long memberId = (Long) session.getAttribute("memberId");

        //     //로그인x시 더미데이터 
        //     if (memberId==null){
        //         memberId = 1L;
        //     }

            projectVO.setOwnerId(1L);

            projectService.registerProject(projectVO);
            
            return "redirect:/project/list";
    }
    
    
    @GetMapping("/my")
    public String my() {
        return "project/my";
    }
}
