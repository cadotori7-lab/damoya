package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
public class ProjectController {
    
    @GetMapping("/list")
    public String list() {
        return "project/list";
    }

    @GetMapping("/detail")
    public String detail() {
        return "project/detail";
    }
    @GetMapping("/form")
    public String form() {
        return "project/form";
    }
    @GetMapping("/my")
    public String my() {
        return "project/my";
    }
}
