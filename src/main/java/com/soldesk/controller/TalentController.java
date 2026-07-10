package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/talent")
public class TalentController {
    
    @GetMapping("/list")
    public String list() {
        return "talent/list";
    }

    @GetMapping("/detail")
    public String detail() {
        return "talent/detail";
    }
    @GetMapping("/form")
    public String form() {
        return "talent/form";
    }
}
