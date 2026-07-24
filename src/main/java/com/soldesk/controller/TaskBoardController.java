package com.soldesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.TaskService;
import com.soldesk.vo.TaskVO;

@Controller
@RequestMapping("/workspace/{project_id}")
public class TaskBoardController {

    @Autowired
    private TaskService taskService;

    // 업무 보드
    @GetMapping("/board")
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

    // 업무 등록 화면
    @GetMapping("/taskform")
    public String taskForm(
            @PathVariable("project_id") long project_id,
            Model model) {

        TaskVO task = new TaskVO();
        task.setProject_id(project_id);

        model.addAttribute("project_id", project_id);
        model.addAttribute("task", task);

        return "workspace/taskform";
    }

    // 업무 등록
    @PostMapping("/tasks")
    public String insertTask(
        @PathVariable("project_id") long project_id,
        @ModelAttribute TaskVO task) {

        taskService.insertTask(project_id, task);

        return "redirect:/workspace/"
            + project_id
            + "/board";
    }

    @GetMapping("/tasks/{task_id}") //업무 상세 조회
    public String taskDetail(
        @PathVariable("project_id") long project_id,
        @PathVariable("task_id") long task_id,
        Model model) {

        TaskVO task = taskService.selectTaskById(
            project_id,
            task_id
        );

        if (task == null) {
            return "redirect:/workspace/"
                    + project_id
                    + "/board";
        }

        model.addAttribute("task", task);
        model.addAttribute("project_id", project_id);

        return "workspace/taskdetail";
    }
}