package com.soldesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 랜딩(비로그인)과 홈 대시보드(로그인)를 나눈 컨트롤러.
 */
@Controller
public class HomeController {

    /**
     * 루트.
     * 지금은 랜딩을 반환한다.
     * Spring Security(B단계) 적용 후에는 로그인 여부로 분기한다:
     *
     *   @GetMapping("/")
     *   public String root(java.security.Principal principal) {
     *       if (principal != null) return "redirect:/home"; // 로그인 → 대시보드
     *       return "landing";                                // 비로그인 → 랜딩
     *   }
     */
    @GetMapping("/")
    public String landing() {
        return "landing"; // -> /WEB-INF/views/landing.jsp
    }

    /**
     * 로그인 회원의 개인 대시보드.
     * (Security 적용 후 이 URL은 인증 필요로 보호한다)
     */
    @GetMapping("/home")
    public String home(Model model) {
        // TODO: 로그인 사용자/집계는 이후 서비스에서 조회
        model.addAttribute("greetingName", "김민재");
        model.addAttribute("myProjectCount", 3);
        model.addAttribute("myTaskCount", 2);
        model.addAttribute("applyCount", 2);
        return "home"; // -> /WEB-INF/views/home.jsp
    }
}