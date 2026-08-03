package com.soldesk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.MemberService;
import com.soldesk.service.MentorService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorDashboardStatsVO;

@Controller
@RequestMapping("/mentor")
public class MentorController {

    private static final Logger logger = LoggerFactory.getLogger(MentorController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private MentorService mentorService;

    @GetMapping({"", "/", "/dashboard"})
    public String mentorDashboard(Model model) {
        logger.info("멘토 대시보드 진입");
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberVO member = memberService.findByLoginId(loginId);
        model.addAttribute("member", member);

        MentorDashboardStatsVO stats = mentorService.getDashboardStats(member.getMember_id());
        model.addAttribute("stats", stats);
        return "mentor/dashboard";
    }
}
