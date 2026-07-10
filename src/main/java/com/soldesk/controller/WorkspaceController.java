package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workspace")
public class WorkspaceController {
    
    @GetMapping("/applicants")
    public String applicants() {
        return "workspace/applicants";
    }

    @GetMapping("/board")
    public String board() {
        return "workspace/board";
    }

    @GetMapping("/complete")
    public String complete() {
        return "workspace/complete";
    }
    
    @GetMapping("/meeting-form")
    public String meetingForm() {
        return "workspace/meeting-form";
    }
    @GetMapping("/meetings")
    public String meetings() {
        return "workspace/meetings";
    }
    @GetMapping("/members")
    public String members() {
        return "workspace/members";
    }
    @GetMapping("/overview")
    public String overview() {
        return "workspace/overview";
    }
    @GetMapping("/results")
    public String results() {
        return "workspace/results";
    }
    @GetMapping("/schedule")
    public String schedule() {
        return "workspace/schedule";
    }
    @GetMapping("/taskform")
    public String taskForm() {
        return "workspace/taskform";
    }
}
