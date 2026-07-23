package com.soldesk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk.service.TaskService;
import com.soldesk.vo.TaskVO;

@Controller
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/board") //업무 보드
    public String board(
            @RequestParam(value = "project_id", required = false) Long project_id,
            Model model) {

        // 개발 중에는 임시 프로젝트 ID 사용
        if (project_id == null) {
            project_id = 16L;
        }

        model.addAttribute(
            "taskList",
            taskService.selectTaskList(project_id)
        );

        model.addAttribute("project_id", project_id);

        return "workspace/board";
    }

    @GetMapping("/taskform") //업무 등록
    public String taskForm(
            @RequestParam("project_id") long project_id,
            Model model) {

        TaskVO task = new TaskVO();
        task.setProject_id(project_id);

        model.addAttribute("task", task);
        model.addAttribute("project_id", project_id);

        return "workspace/taskform";
    }

    @PostMapping("/tasks") //등록 처리
    public String insertTask(
            @RequestParam("project_id") long project_id,
            @ModelAttribute TaskVO task) {

        task.setProject_id(project_id);
        taskService.insertTask(task);

        return "redirect:/workspace/board?project_id=" + project_id;
    }
}