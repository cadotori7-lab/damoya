package com.soldesk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.TaskService;
import com.soldesk.vo.TaskVO;

@Controller
@RequestMapping("/workspace/{project_id}")
public class WorkspaceController {

    @Autowired
    private TaskService taskService;


    @ModelAttribute
    public void addProjectId(
            @PathVariable("project_id") long project_id,
            Model model) {

        model.addAttribute("project_id", project_id);
    }

    @GetMapping("/board") //업무
    public String board(
            @PathVariable("project_id") long project_id,
            Model model) {

        model.addAttribute(
            "taskList",
            taskService.selectTaskList(project_id)
        );

        return "workspace/board";
    }

    @GetMapping("/overview")
    public String overview() {
        return "workspace/overview";
    }

    @GetMapping("/applicants") //지원자 관리
    public String applicants() {
        return "workspace/applicants";
    }

    @GetMapping("/complete") //완료(최종 결과물)
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

    @GetMapping("/members") //팀원관리
    public String members() {
        return "workspace/members";
    }

    @GetMapping("/results") //결과물
    public String results() {
        return "workspace/results";
    }

    @GetMapping("/schedule") //일정
    public String schedule() {
        return "workspace/schedule";
    }

    @GetMapping("/taskform") //업무 등록 폼
    public String taskForm(
            @PathVariable("project_id") long project_id,
            Model model) {

        TaskVO task = new TaskVO();
        task.setProject_id(project_id);

        model.addAttribute("project_id", project_id);
        model.addAttribute("task", task);

        return "workspace/taskform";
    }

    @PostMapping("/tasks") //업무 등록
    public String insertTask(
            @PathVariable("project_id") long project_id,
            @ModelAttribute TaskVO task) {

        task.setProject_id(project_id);
        taskService.insertTask(task);

        return "redirect:/workspace/"
                + project_id
                + "/board";
    }

}