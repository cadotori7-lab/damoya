package com.soldesk.advice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.service.ProjectService;
import com.soldesk.service.TeamManagementService;
import com.soldesk.vo.MemberVO;

/** 모든 뷰에 로그인 회원(member)을 넣어 헤더 알림/아바타가 동작하게 한다. */
@ControllerAdvice
public class CurrentMemberAdvice {

    private static final Pattern WORKSPACE_PATH = Pattern.compile(
            "/workspace/(\\d+)(?:/|$)");

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private TeamManagementService teamManagementService;

    @Autowired
    private ProjectService projectService;

    @ModelAttribute("member")
    public MemberVO currentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        String name = auth.getName();
        MemberVO member = memberMapper.findByLoginId(name);
        if (member == null) {
            member = memberMapper.findByEmail(name);
        }
        return member;
    }

    /** 모든 워크스페이스 탭에서 팀원 관리 메뉴의 노출 권한을 일관되게 제공하고, 프로젝트 참여자가 아니면 접근을 막는다. */
    @ModelAttribute
    public void workspacePermissions(HttpServletRequest request, Model model) {
        Matcher matcher = WORKSPACE_PATH.matcher(request.getRequestURI());
        if (!matcher.find()) {
            return;
        }

        long projectId = Long.parseLong(matcher.group(1));
        model.addAttribute("project_id", projectId);
        model.addAttribute("project", projectService.getProjectById(projectId));
        MemberVO member = currentMember();
        if (member == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        String role = teamManagementService.getProjectRole(
                projectId, member.getMember_id());
        if (role == null) {
            throw new AccessDeniedException("프로젝트 참여자만 접근할 수 있습니다.");
        }
        boolean isLeader = TeamManagementService.LEADER.equals(role);
        boolean isMentor = TeamManagementService.MENTOR.equals(role);
        model.addAttribute("workspaceProjectRole", role);
        model.addAttribute("canViewTeamManagement", isLeader || isMentor);
        model.addAttribute("canManageTeam", isLeader);
        model.addAttribute("isMentor", isMentor);
    }
}
