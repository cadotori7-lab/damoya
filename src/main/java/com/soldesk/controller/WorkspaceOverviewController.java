package com.soldesk.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.soldesk.service.MemberService;
import com.soldesk.service.ParticipationService;
import com.soldesk.service.WorkspaceOverviewService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.WorkspaceOverviewVO;

@Controller
@RequestMapping("/workspace/{projectId}")
public class WorkspaceOverviewController {

    @Autowired
    private WorkspaceOverviewService overviewService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/overview")
    public String overview(
            @PathVariable("projectId") long projectId,
            Principal principal,
            Model model) throws NoHandlerFoundException {

        MemberVO loginMember = principal == null
                ? null
                : memberService.findByLoginId(principal.getName());
        if (loginMember == null
                || !participationService.canReadProject(
                        projectId, loginMember.getMember_id())) {
            throw new AccessDeniedException(
                    "프로젝트 참여자만 개요를 볼 수 있습니다.");
        }

        WorkspaceOverviewVO overview = overviewService.getOverview(projectId);
        if (overview == null) {
            throw new NoHandlerFoundException(
                    "GET", "/workspace/" + projectId + "/overview",
                    HttpHeaders.EMPTY);
        }

        model.addAttribute("project_id", projectId);
        model.addAttribute("overview", overview);
        model.addAttribute("project", overview.getProject());
        return "workspace/overview";
    }
}
