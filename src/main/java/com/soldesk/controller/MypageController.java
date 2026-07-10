package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageController {
    
    @RequestMapping("/index")
    public String index() {
        return "mypage/index";
    }
}
