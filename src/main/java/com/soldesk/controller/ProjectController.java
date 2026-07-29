package com.soldesk.controller;

import com.soldesk.service.ProjectService;
import com.soldesk.vo.ProjectVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/project")
public class ProjectController {

    private final Logger logger = Logger.getLogger(ProjectController.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ProjectService projectService;

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 프로젝트 목록 페이지
    @GetMapping("/list")
    public String list(Model model) {
        List<ProjectVO> projectList = projectService.getAllProjects();
        model.addAttribute("projectList", projectList);
        return "project/list";
    }

    // 프로젝트 상세 페이지
    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long projectId, Model model) {
        ProjectVO project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        return "project/detail";
    }

    // 프로젝트 등록 폼
    @GetMapping("/form")
    public String form() {
        return "project/form";
    }

    // 프로젝트 등록
    @PostMapping("/register")
    public String registerProject(@ModelAttribute ProjectVO projectVO, HttpSession session) {
        projectVO.setOwnerId(1L); // 임시 오너 ID 설정
        projectService.registerProject(projectVO);
        return "redirect:/project/list";
    }

    // 수정 페이지
    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long projectId, Model model) {
        ProjectVO project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("mode", "update");
        return "project/form";
    }

    // 수정 처리
    @PostMapping("/update")
    public String updateForm(@ModelAttribute ProjectVO projectVO) {
        projectService.updateProject(projectVO);
        return "redirect:/project/detail?id=" + projectVO.getProjectId();
    }

    // 삭제 처리
    @GetMapping("/delete")
    public String deleteProject(@RequestParam("id") Long projectId) {
        projectService.deleteProject(projectId);
        return "redirect:/project/list";
    }

    // 멘토 추천 화면 (즉시 표시 → JS가 API로 추천 요청)
    @GetMapping("/mentor-recommend")
    public String mentorRecommend(@RequestParam("id") Long projectId, Model model) {
        ProjectVO project = projectService.getProjectById(projectId);
        if (project == null) {
            return "redirect:/project/list";
        }

        String projectName = safe(project.getTitle());
        String projectDescription = buildMentorMatchDescription(project);

        model.addAttribute("project", project);
        model.addAttribute("projectName", projectName);
        model.addAttribute("projectDescription", projectDescription);
        model.addAttribute("autoMatch", projectDescription.length() >= 10);

        if (projectDescription.length() < 10) {
            model.addAttribute(
                "error",
                "멘토 추천을 위해 프로젝트 태그 또는 소개글이 더 필요해요. (합쳐서 10자 이상)"
            );
        }

        return "project/mentor-recommend";
    }

    // 멘토 추천 API: DB 태그+소개를 합쳐 FastAPI에 요청
    @GetMapping("/mentor-recommend/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> mentorRecommendApi(
            @RequestParam("id") Long projectId) {
        ProjectVO project = projectService.getProjectById(projectId);
        if (project == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok", false,
                "error", "프로젝트를 찾을 수 없습니다."
            ));
        }

        String projectName = safe(project.getTitle());
        String projectDescription = buildMentorMatchDescription(project);
        String reference = "project-" + project.getProjectId() + "-" + UUID.randomUUID();

        if (projectDescription.length() < 10) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok", false,
                "error", "멘토 추천을 위해 프로젝트 태그 또는 소개글이 더 필요해요. (합쳐서 10자 이상)"
            ));
        }

        logger.info(
            "프로젝트 멘토 추천 요청 projectId="
                + projectId
                + ", projectName="
                + projectName
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("reference", reference);
            body.put("projectName", projectName);
            body.put("projectDescription", projectDescription);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = restTemplate.postForObject(
                fastApiBaseUrl + "/mentor-match",
                request,
                Map.class
            );

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("projectName", projectName);
            response.put("projectDescription", projectDescription);
            response.put("reference", reference);
            response.put("matchResult", result == null ? Map.of() : result);
            return ResponseEntity.ok(response);
        } catch (HttpStatusCodeException e) {
            logger.warning("FastAPI 멘토 추천 오류: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                "ok", false,
                "error", "멘토 추천 요청이 실패했습니다: " + e.getResponseBodyAsString()
            ));
        } catch (ResourceAccessException e) {
            logger.warning("FastAPI 멘토 추천 연결 실패: " + e.getMessage());
            return ResponseEntity.status(503).body(Map.of(
                "ok", false,
                "error", "Python 서버에 연결할 수 없습니다. localhost:8501 서버를 확인하세요."
            ));
        }
    }

    //태그 + 소개글을 한 줄로 합쳐 FastAPI projectDescription 으로 보내기
    private String buildMentorMatchDescription(ProjectVO project) {
        String tags = safe(project.getTags());
        String summary = safe(project.getSummary());
        return Stream.of(tags, summary)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining(" "))
            .trim();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    @GetMapping("/my")
    public String my() {
        return "project/my";
    }
}
