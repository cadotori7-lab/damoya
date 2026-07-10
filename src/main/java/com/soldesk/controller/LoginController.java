package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class LoginController {
    
    @GetMapping("/signup")
    public String signup() {
        return "auth/signup"; 
    }
    @GetMapping("/login")
    public String login() {
        return "auth/login"; 
    }
}
