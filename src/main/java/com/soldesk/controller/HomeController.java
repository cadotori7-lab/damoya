package com.soldesk.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.soldesk.service.MemberService;
import com.soldesk.service.NotificationService;
import com.soldesk.service.ParticipationService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.ParticipationVO;

/** 
 * 랜딩(비로그인)과 홈 대시보드(로그인)를 나눈 컨트롤러.
 */
@Controller
public class HomeController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/")
    public String root(Principal principal,Model model) {
        if(principal != null) {
            MemberVO member = memberService.findByLoginId(principal.getName());
            if(member.getRole().equals("ADMIN")) {
                return "redirect:/admin/dashboard";
            }
        }
        if (principal != null) {
            return "redirect:/home";
        }
        return "landing";
    }

    @GetMapping("/home")
    public String home(Model model) {
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberVO member = memberService.findByLoginId(member_id);
        List<ParticipationVO> participatingProjects = participationService.getParticipatingProjectsByMemberId(member.getMember_id(),3);
        List<ParticipationVO> applicationProjects = participationService.getApplicationProjectsByMemberId(member.getMember_id(),3);
        int notiCount = notificationService.countNotificationsByMemberId(member.getMember_id());
        model.addAttribute("notiCount", notiCount);

        model.addAttribute("member", member);
        model.addAttribute("participatingProjects", participatingProjects);
        model.addAttribute("applicationProjects", applicationProjects);
        return "home"; 
    }
}