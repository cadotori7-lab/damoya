package com.soldesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import com.soldesk.service.TaskService; //업무 서비스


@Controller
@RequestMapping("/workspace/{project_id}")
public class WorkspaceController {
    
    @Autowired
    private TaskService taskService;

    @GetMapping("/applicants") //지원자 관리. 맴버 관리 -> 지원자 관리
    public String applicants() {
        return "workspace/applicants";
    }

    
    @GetMapping("/board") //업무 
    public String board(
        @PathVariable("project_id") long project_id,
        Model model) {

    model.addAttribute("project_id", project_id);
    model.addAttribute(
        "taskList",
        taskService.selectTaskList(project_id)
    );

    return "workspace/board";
}

    @GetMapping("/complete") //최종 결과문 제출
    public String complete() {
        return "workspace/complete";
    }
    
    @GetMapping("/meeting-form") //회의 등록 폼
    public String meetingForm() {
        return "workspace/meeting-form";
    }

    @GetMapping("/meetings") //회의
    public String meetings() {
        return "workspace/meetings";
    }

    @GetMapping("/members") //맴버 관리 승계 순위
    public String members() {
        return "workspace/members";
    }

    @GetMapping("/overview") //개요. 첫 프로젝트 입장
    public String overview(
            @PathVariable("project_id") long project_id,
            Model model) {

        model.addAttribute("project_id", project_id);

        return "workspace/overview";
    }

    @GetMapping("/results") //결과물(팀원이 제출한 것들중 승인 된 것 모음)
    public String results() {
        return "workspace/results";
    }
    @GetMapping("/schedule") //일정
    public String schedule() {
        return "workspace/schedule";
    }
    @GetMapping("/taskform") //업무 등록 폼
    public String taskForm() {
        return "workspace/taskform";
    }
}
