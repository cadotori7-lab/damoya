package com.soldesk.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soldesk.service.AdminDashboardService;
import com.soldesk.service.AdminService;
import com.soldesk.service.CommentService;
import com.soldesk.service.MemberService;
import com.soldesk.service.ProjectService;
import com.soldesk.service.ReportService;
import com.soldesk.service.TalentService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.PageBean;
import com.soldesk.vo.ReportVO;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminDashboardService adminDashboardService;
    private final AdminService adminService;
    private final MemberService memberService;
    private final ReportService reportService;
    private final ProjectService projectService;
    private final CommentService commentService;
    private final TalentService talentService;
    private final ObjectMapper objectMapper;

    public AdminController(AdminDashboardService adminDashboardService,
                           AdminService adminService,
                           MemberService memberService,
                           ReportService reportService,
                           ProjectService projectService,
                           CommentService commentService,
                           TalentService talentService,
                           ObjectMapper objectMapper) {
        this.adminDashboardService = adminDashboardService;
        this.adminService = adminService;
        this.memberService = memberService;
        this.reportService = reportService;
        this.projectService = projectService;
        this.commentService = commentService;
        this.talentService = talentService;
        this.objectMapper = objectMapper;
    }

    // 대시보드 표시
    @GetMapping("/dashboard")
    public String admin(Model model) {
        model.addAttribute("dashboard", adminDashboardService.getDashboardStats());
        return "admin/dashboard";
    }

    // 회원 관리
    @GetMapping("/accounts")
    public String accounts(@RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "search", defaultValue = "") String search,
                           @RequestParam(value = "status", defaultValue = "all") String status,
                           @RequestParam(value = "role", defaultValue = "all") String role,
                           Model model) {
        logger.info("회원 관리 요청, page={}, search={}, status={}, role={}", page, search, status, role);
        List<MemberVO> members = memberService.getAllMembers(page, search, status, role);
        PageBean pageBean = memberService.getPageBean(page, search, status, role);

        String json;
        try {
            json = objectMapper.writeValueAsString(members);
        } catch (JsonProcessingException e) {
            logger.error("JSON 변환 오류: {}", e.getMessage());
            json = "[]";
        }

        Map<String, Object> query = new LinkedHashMap<>();
        query.put("page", page);
        query.put("search", search);
        query.put("status", status);
        query.put("role", role);

        String queryJson;
        try {
            queryJson = objectMapper.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            queryJson = "{\"page\":1,\"search\":\"\",\"status\":\"all\",\"role\":\"all\"}";
        }

        model.addAttribute("members", json);
        model.addAttribute("queryJson", queryJson);
        model.addAttribute("pageBean", pageBean);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("role", role);
        return "admin/accounts";
    }

    // 멘토/학교 인증 승인 (계정 관리)
    @PostMapping("/accounts/approve")
    public String approveAccount(@RequestParam("memberId") int memberId,
                                 @RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "search", defaultValue = "") String search,
                                 @RequestParam(value = "status", defaultValue = "all") String status,
                                 @RequestParam(value = "role", defaultValue = "all") String role,
                                 RedirectAttributes redirectAttributes) {
        logger.info("계정 관리 승인 요청: memberId={}", memberId);
        adminService.approveMember(memberId);
        redirectAttributes.addFlashAttribute("msg", "승인했습니다.");
        return "redirect:" + accountsRedirect(page, search, status, role);
    }

    // 계정 정지
    @PostMapping("/accounts/suspend")
    public String suspendMember(@RequestParam("memberId") int memberId,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "search", defaultValue = "") String search,
                                @RequestParam(value = "status", defaultValue = "all") String status,
                                @RequestParam(value = "role", defaultValue = "all") String role,
                                RedirectAttributes redirectAttributes) {
        try {
            adminService.suspendMember(memberId);
            redirectAttributes.addFlashAttribute("msg", "계정을 정지했습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
        }
        return "redirect:" + accountsRedirect(page, search, status, role);
    }

    // 계정 정지 해제
    @PostMapping("/accounts/resume")
    public String resumeMember(@RequestParam("memberId") int memberId,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "search", defaultValue = "") String search,
                               @RequestParam(value = "status", defaultValue = "all") String status,
                               @RequestParam(value = "role", defaultValue = "all") String role,
                               RedirectAttributes redirectAttributes) {
        try {
            adminService.resumeMember(memberId);
            redirectAttributes.addFlashAttribute("msg", "계정 정지를 해제했습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
        }
        return "redirect:" + accountsRedirect(page, search, status, role);
    }

    // 게시물 관리
    @GetMapping("/posts")
    public String posts(Model model) {
        logger.info("게시물 관리 요청");
        model.addAttribute("reports", toReportsJson("PROJECT"));
        model.addAttribute("postReports", toReportsJson("POST"));
        model.addAttribute("commentReports", toReportsJson("COMMENT"));
        return "admin/posts";
    }

    private String toReportsJson(String targetType) {
        List<ReportVO> reports = reportService.getReportsByTargetType(targetType);
        // LocalDateTime JSON 직렬화 이슈 방지 (날짜는 이미 포맷된 projectCreatedAt/commentCreatedAt으로 노출)
        for (ReportVO report : reports) {
            report.setCreatedAt(null);
        }
        try {
            return objectMapper.writeValueAsString(reports);
        } catch (JsonProcessingException e) {
            logger.error("신고 JSON 변환 오류: {}", e.getMessage());
            return "[]";
        }
    }

    // 게시물 숨김 (해당 프로젝트 신고 status → PROCESSED)
    @PostMapping("/posts/hide")
    public String hidePost(@RequestParam("projectId") Long projectId,
                           RedirectAttributes redirectAttributes) {
        logger.info("게시물 숨김 요청: projectId={}", projectId);
        reportService.updateReportStatusByTarget("PROJECT", projectId, "PROCESSED");
        redirectAttributes.addFlashAttribute("msg", "게시물을 숨김 처리했습니다.");
        return "redirect:/admin/posts";
    }

    // 게시물 복원 (해당 프로젝트 신고 status → RECEIVED)
    @PostMapping("/posts/restore")
    public String restorePost(@RequestParam("projectId") Long projectId,
                              RedirectAttributes redirectAttributes) {
        logger.info("게시물 복원 요청: projectId={}", projectId);
        reportService.updateReportStatusByTarget("PROJECT", projectId, "RECEIVED");
        redirectAttributes.addFlashAttribute("msg", "게시물을 복원했습니다.");
        return "redirect:/admin/posts";
    }

    // 게시물 완전 삭제 (신고 + 프로젝트)
    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam("projectId") Long projectId,
                             RedirectAttributes redirectAttributes) {
        logger.info("게시물 완전 삭제 요청: projectId={}", projectId);
        reportService.deleteReport("PROJECT", projectId);
        projectService.deleteProject(projectId);
        redirectAttributes.addFlashAttribute("msg", "게시물을 삭제했습니다.");
        return "redirect:/admin/posts";
    }

    // 댓글 신고 처리 완료 (댓글은 유지하고 신고만 처리 상태로 변경)
    @PostMapping("/comments/resolve")
    public String resolveCommentReport(@RequestParam("commentId") Long commentId,
                                       RedirectAttributes redirectAttributes) {
        logger.info("댓글 신고 처리 요청: commentId={}", commentId);
        reportService.updateReportStatusByTarget("COMMENT", commentId, "PROCESSED");
        redirectAttributes.addFlashAttribute("msg", "댓글 신고를 처리 완료로 표시했습니다.");
        return "redirect:/admin/posts";
    }

    // 댓글 신고 재검토 (처리 완료 → 미처리로 되돌림)
    @PostMapping("/comments/reopen")
    public String reopenCommentReport(@RequestParam("commentId") Long commentId,
                                      RedirectAttributes redirectAttributes) {
        logger.info("댓글 신고 재검토 요청: commentId={}", commentId);
        reportService.updateReportStatusByTarget("COMMENT", commentId, "RECEIVED");
        redirectAttributes.addFlashAttribute("msg", "댓글 신고를 미처리로 되돌렸습니다.");
        return "redirect:/admin/posts";
    }

    // 댓글 완전 삭제 (신고 + 댓글)
    @PostMapping("/comments/delete")
    public String deleteCommentReport(@RequestParam("commentId") Long commentId,
                                      RedirectAttributes redirectAttributes) {
        logger.info("댓글 완전 삭제 요청: commentId={}", commentId);
        reportService.deleteReport("COMMENT", commentId);
        commentService.deleteComment(commentId);
        redirectAttributes.addFlashAttribute("msg", "댓글을 삭제했습니다.");
        return "redirect:/admin/posts";
    }

    // 인재풀 게시글 신고 처리 완료 (게시글은 유지하고 신고만 처리 상태로 변경)
    @PostMapping("/talents/resolve")
    public String resolveTalentReport(@RequestParam("postId") Long postId,
                                      RedirectAttributes redirectAttributes) {
        logger.info("인재풀 신고 처리 요청: postId={}", postId);
        reportService.updateReportStatusByTarget("POST", postId, "PROCESSED");
        redirectAttributes.addFlashAttribute("msg", "인재풀 게시글 신고를 처리 완료로 표시했습니다.");
        return "redirect:/admin/posts";
    }

    // 인재풀 게시글 신고 재검토 (처리 완료 → 미처리로 되돌림)
    @PostMapping("/talents/reopen")
    public String reopenTalentReport(@RequestParam("postId") Long postId,
                                     RedirectAttributes redirectAttributes) {
        logger.info("인재풀 신고 재검토 요청: postId={}", postId);
        reportService.updateReportStatusByTarget("POST", postId, "RECEIVED");
        redirectAttributes.addFlashAttribute("msg", "인재풀 게시글 신고를 미처리로 되돌렸습니다.");
        return "redirect:/admin/posts";
    }

    // 인재풀 게시글 완전 삭제 (신고 + 게시글)
    @PostMapping("/talents/delete")
    public String deleteTalentReport(@RequestParam("postId") Long postId,
                                     RedirectAttributes redirectAttributes) {
        logger.info("인재풀 게시글 완전 삭제 요청: postId={}", postId);
        reportService.deleteReport("POST", postId);
        talentService.deleteTalent(postId);
        redirectAttributes.addFlashAttribute("msg", "인재풀 게시글을 삭제했습니다.");
        return "redirect:/admin/posts";
    }

    // 학교 인증 승인
    @PostMapping("/approve-member")
    public String approveMember(@RequestParam("memberId") int memberId) {
        logger.info("학교 인증 승인 요청: memberId={}", memberId);
        adminService.approveMember(memberId);
        return "redirect:/admin/dashboard";
    }

    // 학교 인증 반려
    @PostMapping("/reject-member")
    public String rejectMember(@RequestParam("memberId") int memberId) {
        logger.info("학교 인증 반려 요청: memberId={}", memberId);
        adminService.rejectMember(memberId);
        return "redirect:/admin/dashboard";
    }

    private String accountsRedirect(int page, String search, String status, String role) {
        return UriComponentsBuilder.fromPath("/admin/accounts")
                .queryParam("page", page)
                .queryParam("search", search)
                .queryParam("status", status)
                .queryParam("role", role)
                .build()
                .toUriString();
    }
}
