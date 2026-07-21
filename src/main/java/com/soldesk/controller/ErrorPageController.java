package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Spring Security의 accessDeniedPage("/error/403") forward 대상.
 * forward는 원래 요청의 HTTP 메서드를 그대로 유지하므로 메서드를 제한하지 않는다.
 */
@Controller
public class ErrorPageController {

    @RequestMapping("/error/403")
    public String forbidden() {
        return "error/403";
    }
}
