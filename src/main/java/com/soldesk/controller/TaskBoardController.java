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
import com.soldesk.service.ParticipationService;
import com.soldesk.vo.TaskVO;

import java.security.Principal;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.service.MemberService;
import com.soldesk.vo.MemberVO;

@Controller
@RequestMapping("/workspace/{project_id}")
public class TaskBoardController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private MemberService memberService;

    // 업무 보드
    @GetMapping("/board")
    public String board(
        @PathVariable("project_id") long project_id,
        Principal principal,
        Model model) {

        MemberVO loginMember = null;

        if (principal != null) {
            loginMember = memberService.findByLoginId(
                principal.getName()
            );
        }

        boolean isLeader = loginMember != null
            && participationService.isLeader(
                project_id,
                loginMember.getMember_id()
        );

        model.addAttribute("project_id", project_id);
        model.addAttribute("isLeader", isLeader);

        model.addAttribute(
            "taskList",
            taskService.selectTaskList(project_id)
        );

        model.addAttribute(
            "currentMemberId",
            loginMember != null
            ? loginMember.getMember_id()
            : 0
        );

        return "workspace/board";
    }

    // 업무 등록 화면
    @GetMapping("/taskform")
    public String taskForm(
        @PathVariable("project_id") long project_id,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes) {

        MemberVO loginMember = null;

        if (principal != null) {
            loginMember = memberService.findByLoginId(
                principal.getName()
            );
        }

        if (loginMember == null
            || !participationService.isLeader(
                    project_id,
                    loginMember.getMember_id())) {

            redirectAttributes.addFlashAttribute(
                "taskError",
                "프로젝트 팀장만 업무를 등록할 수 있습니다."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        TaskVO task = new TaskVO();
        task.setProject_id(project_id);

        model.addAttribute("project_id", project_id);
        model.addAttribute("task", task);

        model.addAttribute(
            "projectMembers",
            participationService.selectTaskMembers(project_id)
        );

        return "workspace/taskform";
    }

    // 업무 등록
    @PostMapping("/tasks")
    public String insertTask(
        @PathVariable("project_id") long project_id,
        @ModelAttribute("task") TaskVO task,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes) {

        task.setProject_id(project_id);

        // 현재 로그인 사용자 조회
        MemberVO loginMember = null;

        if (principal != null) {
            loginMember = memberService.findByLoginId(
                principal.getName()
            );
        }

        // 로그인 정보가 없거나 프로젝트 팀장이 아닌 경우
        if (loginMember == null
            || !participationService.isLeader(
                    project_id,
                    loginMember.getMember_id())) {

            redirectAttributes.addFlashAttribute(
                "taskError",
                "프로젝트 팀장만 업무를 등록할 수 있습니다."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        // 선택한 담당자가 해당 프로젝트 참여자인지 검사
        Long assigneeId = task.getAssignee_id();

        if (assigneeId == null
            || !participationService.isTaskMember(
                    project_id,
                    assigneeId)) {

            model.addAttribute("project_id", project_id);

            model.addAttribute(
                "projectMembers",
                participationService.selectTaskMembers(project_id)
            );

            model.addAttribute(
                "assigneeError",
                "해당 프로젝트의 참여자만 담당자로 지정할 수 있습니다."
            );

            return "workspace/taskform";
        }

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

    //업무제출
    @PostMapping("/tasks/{task_id}/submit")
    public String submitTask(
        @PathVariable("project_id") long project_id,
        @PathVariable("task_id") long task_id,
        @ModelAttribute TaskVO task,
        Principal principal,
        RedirectAttributes redirectAttributes) {

        MemberVO loginMember = null;

        if (principal != null) {
            loginMember = memberService.findByLoginId(
                principal.getName()
            );
        }

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute(
                "taskError",
                "로그인이 필요합니다."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        String submitTitle = task.getSubmit_title();
        String submitContent = task.getSubmit_content();

        if (submitTitle == null || submitTitle.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "taskError",
                "제출 제목을 입력해주세요."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        if (submitContent == null || submitContent.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "taskError",
                "제출 내용을 입력해주세요."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        // URL과 로그인 정보로 서버에서 값을 고정
        task.setTask_id(task_id);
        task.setProject_id(project_id);
        task.setAssignee_id(
            (long) loginMember.getMember_id()
        );

        task.setSubmit_title(submitTitle.trim());
        task.setSubmit_content(submitContent.trim());

        boolean submitted = taskService.submitTask(task);

        if (!submitted) {
            redirectAttributes.addFlashAttribute(
                "taskError",
                "본인에게 배정된 진행 중 또는 반려된 업무만 제출할 수 있습니다."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        redirectAttributes.addFlashAttribute(
            "taskMessage",
            "업무 결과물을 제출했습니다. 팀장의 검수를 기다려주세요."
        );

        return "redirect:/workspace/"
            + project_id
            + "/board";
    }
}