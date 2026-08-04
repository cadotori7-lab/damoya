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
import com.soldesk.service.MemberService;
import com.soldesk.service.ProjectService;
import com.soldesk.service.ReportService;
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
    private final ObjectMapper objectMapper;

    public AdminController(AdminDashboardService adminDashboardService,
                           AdminService adminService,
                           MemberService memberService,
                           ReportService reportService,
                           ProjectService projectService,
                           ObjectMapper objectMapper) {
        this.adminDashboardService = adminDashboardService;
        this.adminService = adminService;
        this.memberService = memberService;
        this.reportService = reportService;
        this.projectService = projectService;
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
        List<ReportVO> projectReports = reportService.getReportsByTargetType("PROJECT");
        List<ReportVO> postReports = reportService.getReportsByTargetType("POST");
        List<ReportVO> commentReports = reportService.getReportsByTargetType("COMMENT");
        // LocalDateTime JSON 직렬화 이슈 방지
        for (ReportVO report : projectReports) {
            report.setCreatedAt(null);
        }
        for (ReportVO report : postReports) {
            report.setCreatedAt(null);
        }
        for (ReportVO report : commentReports) {
            report.setCreatedAt(null);
        }

        String reportsJson;
        String reportsJson2;
        String reportsJson3;
        try {
            reportsJson = objectMapper.writeValueAsString(projectReports);
            reportsJson2 = objectMapper.writeValueAsString(postReports);
            reportsJson3 = objectMapper.writeValueAsString(commentReports);
        } catch (JsonProcessingException e) {
            logger.error("신고 JSON 변환 오류: {}", e.getMessage());
            reportsJson = "[]";
            reportsJson2 = "[]";
            reportsJson3 = "[]";
        }

        model.addAttribute("reports", reportsJson);
        model.addAttribute("postReports", reportsJson2);
        model.addAttribute("commentReports", reportsJson3);
        return "admin/posts";
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
