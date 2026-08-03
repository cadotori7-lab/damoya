package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.AdminDashboardService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminDashboardService adminDashboardService;

    public AdminController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    // 대시보드 표시
    @GetMapping("/dashboard")
    public String admin(Model model) {
        model.addAttribute("dashboard", adminDashboardService.getDashboardStats());
        return "admin/dashboard";
    }
    @GetMapping("/accounts")
    public String accounts() {
        return "admin/accounts";
    }
    @GetMapping("/posts")
    public String posts() {
        return "admin/posts";
    }

}