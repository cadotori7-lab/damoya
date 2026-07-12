package com.soldesk.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.soldesk.service.MemberService;
import com.soldesk.vo.MemberVO;

/**
 * 랜딩(비로그인)과 홈 대시보드(로그인)를 나눈 컨트롤러.
 */
@Controller
public class HomeController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/")
    public String root(Principal principal) {
        if (principal != null) {
            return "redirect:/home";
        }
        return "landing"; // -> /WEB-INF/views/landing.jsp
    }

    /**
     * 로그인 회원의 개인 대시보드.
     * (Security 적용 후 이 URL은 인증 필요로 보호한다)
     */
    @GetMapping("/home")
    public String home(Model model) {
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberVO member = memberService.findByLoginId(member_id);
        model.addAttribute("member", member);
        return "home"; // -> /WEB-INF/views/home.jsp
    }
}