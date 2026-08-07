package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/workspace/{project_id}")
public class WorkspaceController {

    @ModelAttribute
    public void addProjectId(
            @PathVariable("project_id") long project_id,
            Model model) {

        model.addAttribute("project_id", project_id);
    }

    @GetMapping("/meeting-form") //회의 등록 폼
    public String meetingForm() {
        return "workspace/meeting-form";
    }

}
