package com.soldesk.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.FeedbackService;
import com.soldesk.service.MemberService;
import com.soldesk.service.MentorService;
import com.soldesk.service.ParticipationService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorDashboardStatsVO;
import com.soldesk.vo.ParticipationVO;

@Controller
@RequestMapping("/mentor")
public class MentorController {

    private static final Logger logger = LoggerFactory.getLogger(MentorController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private MentorService mentorService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping({"", "/", "/dashboard"})
    public String mentorDashboard(Model model) {
        logger.info("멘토 대시보드 진입");
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberVO member = memberService.findByLoginId(loginId);
        model.addAttribute("member", member);

        MentorDashboardStatsVO stats = mentorService.getDashboardStats(member.getMember_id());
        model.addAttribute("stats", stats);
        model.addAttribute("teams", mentorService.getMentoringTeams(member.getMember_id()));

        // 수락 대기 중인 멘토 제안 (인재풀 팀원 제의는 제외해 상단 카운트와 기준을 맞춘다)
        List<ParticipationVO> offeredProjects = participationService
                .getOfferedProjectsByMemberId(member.getMember_id(), 0)
                .stream()
                .filter(o -> "MENTOR".equals(o.getProjectRole()))
                .collect(Collectors.toList());
        model.addAttribute("offeredProjects", offeredProjects);
        model.addAttribute("myFeedbacks", feedbackService.getFeedbacksByMentor(member.getMember_id()));
        return "mentor/dashboard";
    }
}
