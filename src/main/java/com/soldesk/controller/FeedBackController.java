package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/feedback")
public class FeedBackController {

    @GetMapping("/feedback")
    public String list() {
        return "feedback/feedback";
    }
    
}
