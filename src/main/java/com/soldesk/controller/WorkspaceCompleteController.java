package com.soldesk.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.service.FinalResultService;
import com.soldesk.service.MemberService;
import com.soldesk.service.ParticipationService;
import com.soldesk.vo.MemberVO;

@Controller
@RequestMapping("/workspace/{projectId}")
public class WorkspaceCompleteController {

    @Autowired
    private FinalResultService finalResultService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/complete")
    public String complete(
            @PathVariable("projectId") long projectId,
            Principal principal,
            Model model) {

        MemberVO loginMember = requireProjectMember(projectId, principal);
        model.addAttribute("project_id", projectId);
        model.addAttribute(
                "isLeader",
                participationService.isLeader(
                        projectId, loginMember.getMember_id()));
        model.addAttribute(
                "completion",
                finalResultService.getCompletionOverview(projectId));
        return "workspace/complete";
    }

    @PostMapping("/complete")
    public String submitFinalResult(
            @PathVariable("projectId") long projectId,
            @RequestParam(value = "submitTitle", required = false)
            String submitTitle,
            @RequestParam(value = "submitDescription", required = false)
            String submitDescription,
            @RequestParam(value = "reflection", required = false)
            String reflection,
            @RequestParam(value = "finalFile", required = false)
            MultipartFile finalFile,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        MemberVO loginMember = requireProjectMember(projectId, principal);
        if (!participationService.isLeader(
                projectId, loginMember.getMember_id())) {
            throw new AccessDeniedException(
                    "프로젝트 팀장만 최종 결과물을 제출할 수 있습니다.");
        }

        try {
            finalResultService.submitFinalResult(
                    projectId,
                    loginMember.getMember_id(),
                    submitTitle,
                    submitDescription,
                    reflection,
                    finalFile);
            redirectAttributes.addFlashAttribute(
                    "completeMessage",
                    "최종 결과물을 제출하고 프로젝트를 완료했습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute(
                    "completeError", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(
                    "completeError",
                    "최종 결과물 파일 저장 중 오류가 발생했습니다.");
        }

        return "redirect:/workspace/" + projectId + "/complete";
    }

    private MemberVO requireProjectMember(
            long projectId, Principal principal) {
        MemberVO loginMember = principal == null
                ? null
                : memberService.findByLoginId(principal.getName());
        if (loginMember == null
                || !participationService.canReadProject(
                        projectId, loginMember.getMember_id())) {
            throw new AccessDeniedException(
                    "프로젝트 참여자만 완료 화면을 볼 수 있습니다.");
        }
        return loginMember;
    }
}
