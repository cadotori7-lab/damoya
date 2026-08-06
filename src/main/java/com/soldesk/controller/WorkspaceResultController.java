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
import com.soldesk.service.ProjectService;
import com.soldesk.service.ResultService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.ProjectVO;

@Controller
@RequestMapping("/workspace/{projectId}")
public class WorkspaceResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/results")
    public String results(
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
                    "프로젝트 참여자만 결과물을 볼 수 있습니다.");
        }

        ProjectVO project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new NoHandlerFoundException(
                    "GET", "/workspace/" + projectId + "/results",
                    HttpHeaders.EMPTY);
        }

        model.addAttribute("project_id", projectId);
        model.addAttribute("project", project);
        model.addAttribute(
                "results", resultService.getApprovedResults(projectId));
        model.addAttribute(
                "teamMembers",
                participationService.selectTaskMembers(projectId));

        return "workspace/results";
    }
}
