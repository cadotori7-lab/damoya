package com.soldesk.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.service.MemberService;
import com.soldesk.service.ProjectService;
import com.soldesk.service.TeamManagementService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.ParticipationVO;

@Controller
@RequestMapping("/workspace/{projectId}")
public class TeamManagementController {

    private final TeamManagementService teamManagementService;
    private final MemberService memberService;
    private final ProjectService projectService;

    public TeamManagementController(TeamManagementService teamManagementService,
                                    MemberService memberService,
                                    ProjectService projectService) {
        this.teamManagementService = teamManagementService;
        this.memberService = memberService;
        this.projectService = projectService;
    }

    @GetMapping("/members")
    public String members(@PathVariable("projectId") long projectId,
                          Principal principal, Model model) {
        MemberVO actor = requireLoginMember(principal);
        String role = teamManagementService.getProjectRole(
                projectId, actor.getMember_id());
        if (!TeamManagementService.LEADER.equals(role)
                && !TeamManagementService.MENTOR.equals(role)) {
            throw new AccessDeniedException("팀장과 멘토만 팀원 관리를 볼 수 있습니다.");
        }

        model.addAttribute("project_id", projectId);
        model.addAttribute("project", projectService.getProjectById(projectId));
        List<ParticipationVO> teamMembers = teamManagementService.getTeamMembers(
                projectId, actor.getMember_id());
        model.addAttribute("teamMembers", teamMembers);
        model.addAttribute("currentProjectRole", role);
        model.addAttribute("canManageTeam", TeamManagementService.LEADER.equals(role));
        if (TeamManagementService.LEADER.equals(role)) {
            model.addAttribute("canLeaderLeave", teamMembers.stream()
                    .anyMatch(member -> "MEMBER".equals(member.getProjectRole())));
            model.addAttribute("waitingApplicantCount",
                    teamManagementService.getWaitingApplicants(
                            projectId, actor.getMember_id()).size());
            model.addAttribute("offeredMemberCount",
                    teamManagementService.getOfferedMembers(
                            projectId, actor.getMember_id()).size());
        }
        return "workspace/members";
    }

    @GetMapping("/applicants")
    public String applicants(@PathVariable("projectId") long projectId,
                             @RequestParam(value = "tab", defaultValue = "applicants") String tab,
                             Principal principal, Model model) {
        MemberVO actor = requireLoginMember(principal);
        List<ParticipationVO> applicants = teamManagementService.getWaitingApplicants(
                projectId, actor.getMember_id());
        List<ParticipationVO> offeredMembers = teamManagementService.getOfferedMembers(
                projectId, actor.getMember_id());
        model.addAttribute("project_id", projectId);
        model.addAttribute("project", projectService.getProjectById(projectId));
        model.addAttribute("applicants", applicants);
        model.addAttribute("offeredMembers", offeredMembers);
        model.addAttribute("activeTab", "offers".equals(tab) ? "offers" : "applicants");
        return "workspace/applicants";
    }

    @PostMapping("/applicants/{memberId}/approve")
    public String approveApplicant(@PathVariable("projectId") long projectId,
                                   @PathVariable("memberId") long memberId,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        MemberVO actor = requireLoginMember(principal);
        try {
            teamManagementService.approveApplicant(
                    projectId, memberId, actor.getMember_id());
            redirectAttributes.addFlashAttribute(
                    "teamMessage", "지원자를 팀원으로 승인했습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("teamError", e.getMessage());
        }
        return applicantsRedirect(projectId);
    }

    @PostMapping("/applicants/{memberId}/reject")
    public String rejectApplicant(@PathVariable("projectId") long projectId,
                                  @PathVariable("memberId") long memberId,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        MemberVO actor = requireLoginMember(principal);
        try {
            teamManagementService.rejectApplicant(
                    projectId, memberId, actor.getMember_id());
            redirectAttributes.addFlashAttribute(
                    "teamMessage", "지원서를 거절했습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("teamError", e.getMessage());
        }
        return applicantsRedirect(projectId);
    }

    @PostMapping("/members/{memberId}/kick")
    public String kickMember(@PathVariable("projectId") long projectId,
                             @PathVariable("memberId") long memberId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        MemberVO actor = requireLoginMember(principal);
        try {
            teamManagementService.kickMember(
                    projectId, memberId, actor.getMember_id());
            redirectAttributes.addFlashAttribute(
                    "teamMessage", "팀원을 강퇴했습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("teamError", e.getMessage());
        }
        return membersRedirect(projectId);
    }

    @PostMapping("/members/{memberId}/succession")
    public String moveSuccession(@PathVariable("projectId") long projectId,
                                 @PathVariable("memberId") long memberId,
                                 @RequestParam("direction") String direction,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        MemberVO actor = requireLoginMember(principal);
        try {
            teamManagementService.moveSuccessionOrder(
                    projectId, memberId, direction, actor.getMember_id());
            redirectAttributes.addFlashAttribute(
                    "teamMessage", "승계 순위를 변경했습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("teamError", e.getMessage());
        }
        return membersRedirect(projectId);
    }

    @PostMapping("/members/leave")
    public String leaveAsLeader(@PathVariable("projectId") long projectId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        MemberVO actor = requireLoginMember(principal);
        try {
            teamManagementService.leaveProjectAsLeader(
                    projectId, actor.getMember_id());
            redirectAttributes.addFlashAttribute(
                    "msg", "프로젝트에서 나갔습니다. 승계 1순위 팀원이 새 팀장이 되었습니다.");
            return "redirect:/project/my";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("teamError", e.getMessage());
            return membersRedirect(projectId);
        }
    }

    private MemberVO requireLoginMember(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        MemberVO member = memberService.findByLoginId(principal.getName());
        if (member == null) {
            throw new AccessDeniedException("회원 정보를 찾을 수 없습니다.");
        }
        return member;
    }

    private String membersRedirect(long projectId) {
        return "redirect:/workspace/" + projectId + "/members";
    }

    private String applicantsRedirect(long projectId) {
        return "redirect:/workspace/" + projectId + "/applicants";
    }
}
