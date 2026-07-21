package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.ElasticSearchService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ElasticSearchService elasticSearchService;

    public AdminController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @GetMapping("/dashboard")
    public String admin(Model model) {
        model.addAttribute("dashboard", elasticSearchService.getDashboardStats());
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
