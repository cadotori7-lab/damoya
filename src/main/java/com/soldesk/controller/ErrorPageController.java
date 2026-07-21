package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController {

    @RequestMapping("/error/403")
    public String forbidden() {
        return "error/403";
    }
}
