package com.soldesk.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.service.FeedbackService;
import com.soldesk.service.MemberService;
import com.soldesk.service.MentorService;
import com.soldesk.service.ProjectService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.MentorTeamVO;
import com.soldesk.vo.ProjectVO;

@Controller
@RequestMapping("/feedback")
public class FeedBackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedBackController.class);

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MentorService mentorService;

    // 피드백 작성/조회 화면
    @GetMapping("/{projectId}")
    public String feedbackPage(@PathVariable("projectId") long projectId, Model model) {
        MemberVO mentor = currentMember();

        ProjectVO project = projectService.getProjectById(projectId);
        if (project == null) {
            return "redirect:/mentor/dashboard";
        }
        if (!feedbackService.isProjectMentor(projectId, mentor.getMember_id())) {
            throw new AccessDeniedException("해당 프로젝트의 멘토만 접근할 수 있습니다.");
        }

        // 진행도/업무 요약: 멘토링 팀 목록 쿼리에서 해당 프로젝트만 사용
        MentorTeamVO teamSummary = mentorService.getMentoringTeams(mentor.getMember_id())
                .stream()
                .filter(t -> t.getProjectId() == projectId)
                .findFirst()
                .orElse(null);

        model.addAttribute("member", mentor);
        model.addAttribute("project", project);
        model.addAttribute("teamSummary", teamSummary);
        model.addAttribute("dday", calcDday(project.getEndDate()));
        model.addAttribute("feedbacks", feedbackService.getFeedbacksByProject(projectId));
        return "feedback/feedback";
    }

    // 피드백 등록
    @PostMapping("/{projectId}")
    public String writeFeedback(@PathVariable("projectId") long projectId,
                                @RequestParam("stage") String stage,
                                @RequestParam("content") String content,
                                RedirectAttributes ra) {
        MemberVO mentor = currentMember();
        try {
            feedbackService.writeFeedback(projectId, mentor.getMember_id(), stage, content);
            ra.addFlashAttribute("msg", "피드백을 등록했어요.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/feedback/" + projectId;
    }

    private MemberVO currentMember() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberService.findByLoginId(loginId);
    }

    // 종료일까지 남은 일수. 종료일이 없거나 형식이 다르면 null
    private Long calcDday(String endDate) {
        if (endDate == null || endDate.isEmpty()) {
            return null;
        }
        try {
            LocalDate end = LocalDate.parse(endDate.length() > 10 ? endDate.substring(0, 10) : endDate);
            return ChronoUnit.DAYS.between(LocalDate.now(), end);
        } catch (Exception e) {
            logger.debug("종료일 파싱 실패: {}", endDate);
            return null;
        }
    }
}
