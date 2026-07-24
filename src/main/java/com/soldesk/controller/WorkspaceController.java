package com.soldesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.TaskService;

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

    @GetMapping("/board")
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

    @GetMapping("/applicants")
    public String applicants() {
        return "workspace/applicants";
    }

    @GetMapping("/complete")
    public String complete() {
        return "workspace/complete";
    }

    @GetMapping("/meeting-form")
    public String meetingForm() {
        return "workspace/meeting-form";
    }

    @GetMapping("/members")
    public String members() {
        return "workspace/members";
    }

    @GetMapping("/results")
    public String results() {
        return "workspace/results";
    }

@GetMapping("/taskform")
    public String taskForm() {
        return "workspace/taskform";
    }
}