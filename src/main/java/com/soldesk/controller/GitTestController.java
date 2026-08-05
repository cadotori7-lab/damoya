package com.soldesk.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.soldesk.service.GitHubOAuthService;

@Controller
@RequestMapping("/gitTest")
public class GitTestController {

    private static final Logger logger = LoggerFactory.getLogger(GitTestController.class);
    private static final Pattern REF_PATTERN = Pattern.compile(
        "^(?!-)(?!.*(?:^|/)\\.\\.(?:/|$))(?!.*//)[A-Za-z0-9._/-]{1,200}$"
    );
    private static final Pattern REPOSITORY_SEGMENT = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final RestTemplate restTemplate = new RestTemplate();
    private final GitHubOAuthService gitHubOAuthService;

    public GitTestController(GitHubOAuthService gitHubOAuthService) {
        this.gitHubOAuthService = gitHubOAuthService;
    }

    @Value("${fastapi.base-url:http://localhost:8501}")
    private String fastApiBaseUrl;

    @GetMapping({"", "/"})
    public String gitTest(Model model, HttpSession session) {
        populateGitHub(model, session);
        return "gitTest";
    }

    @PostMapping(
        value = "/analyze",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public String requestAnalysis(
            @RequestParam("repositoryUrl") String repositoryUrl,
            @RequestParam(value = "projectName", required = false) String projectName,
            @RequestParam(value = "ref", required = false) String ref,
            Model model,
            HttpSession session) {
        populateGitHub(model, session);
        String githubToken = gitHubOAuthService.getAccessToken(session);
        if (githubToken == null) {
            model.addAttribute("error", "GitHub 로그인 후에만 PDF를 만들 수 있습니다.");
            return "gitTest";
        }

        String normalizedProjectName = projectName == null ? "" : projectName.trim();
        String normalizedRef = ref == null ? "" : ref.trim();
        String normalizedRepositoryUrl;

        try {
            normalizedRepositoryUrl = normalizeRepositoryUrl(repositoryUrl);
        } catch (IllegalArgumentException error) {
            return showFormError(
                model,
                repositoryUrl,
                normalizedProjectName,
                normalizedRef,
                error.getMessage()
            );
        }

        if (normalizedProjectName.isEmpty()) {
            normalizedProjectName = normalizedRepositoryUrl.substring(
                normalizedRepositoryUrl.lastIndexOf('/') + 1
            );
        }

        if (normalizedProjectName.length() > 200) {
            return showFormError(
                model,
                normalizedRepositoryUrl,
                normalizedProjectName,
                normalizedRef,
                "프로젝트 이름은 200자 이하여야 합니다."
            );
        }
        if (!normalizedRef.isEmpty() && !REF_PATTERN.matcher(normalizedRef).matches()) {
            return showFormError(
                model,
                normalizedRepositoryUrl,
                normalizedProjectName,
                normalizedRef,
                "브랜치 또는 태그 형식이 올바르지 않습니다."
            );
        }

        populateForm(model, normalizedRepositoryUrl, normalizedProjectName, normalizedRef);
        try {
            Map<String, Object> report = createReport(
                normalizedRepositoryUrl,
                normalizedProjectName,
                normalizedRef,
                githubToken
            );
            model.addAttribute("submitted", true);
            model.addAttribute("reportResult", report);
        } catch (HttpStatusCodeException error) {
            logger.warn(
                "Git 리포트 API가 상태 코드 {}로 실패했습니다.",
                error.getStatusCode().value()
            );
            model.addAttribute(
                "error",
                "프로젝트 분석에 실패했습니다. 저장소 접근 권한과 브랜치를 확인해 주세요."
            );
        } catch (ResourceAccessException error) {
            logger.warn("Git 리포트 서비스 연결 실패: {}", error.getMessage());
            model.addAttribute(
                "error",
                "Python MCP 서버에 연결할 수 없습니다. FastAPI 서버 상태를 확인해 주세요."
            );
        } catch (RuntimeException error) {
            logger.error("Git 프로젝트 리포트 생성 중 오류가 발생했습니다.", error);
            model.addAttribute("error", "Git 프로젝트 리포트 생성에 실패했습니다.");
        }
        return "gitTest";
    }

    private Map<String, Object> createReport(
            String repositoryUrl,
            String projectName,
            String ref,
            String githubToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        Map<String, String> body = new HashMap<>();
        body.put("repositoryUrl", repositoryUrl);
        body.put("projectName", projectName);
        if (!ref.isEmpty()) {
            body.put("ref", ref);
        }
        if (githubToken != null) {
            body.put("githubToken", githubToken);
        }

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            normalizeFastApiBaseUrl() + "/git-report",
            HttpMethod.POST,
            new HttpEntity<>(body, headers),
            new ParameterizedTypeReference<Map<String, Object>>() { }
        );
        Map<String, Object> result = response.getBody();
        if (result == null || result.get("reportId") == null) {
            throw new IllegalStateException("Git 리포트 서버 응답이 올바르지 않습니다.");
        }
        return result;
    }

    private String showFormError(
            Model model,
            String repositoryUrl,
            String projectName,
            String ref,
            String message) {
        populateForm(
            model,
            repositoryUrl == null ? "" : repositoryUrl,
            projectName,
            ref
        );
        model.addAttribute("error", message);
        return "gitTest";
    }

    private void populateForm(
            Model model,
            String repositoryUrl,
            String projectName,
            String ref) {
        model.addAttribute("repositoryUrl", repositoryUrl);
        model.addAttribute("projectName", projectName);
        model.addAttribute("ref", ref);
    }

    private void populateGitHub(Model model, HttpSession session) {
        String login = gitHubOAuthService.getLogin(session);
        boolean connected = gitHubOAuthService.getAccessToken(session) != null;
        model.addAttribute("githubConnected", connected);
        model.addAttribute("githubLogin", login);
        if (connected) {
            try {
                model.addAttribute("githubRepositories", gitHubOAuthService.listRepositories(session));
            } catch (RuntimeException error) {
                logger.warn("GitHub 저장소 목록 조회 실패: {}", error.getMessage());
                model.addAttribute("githubError", "GitHub 저장소 목록을 불러오지 못했습니다. 다시 로그인해 주세요.");
            }
        }
    }

    private String normalizeFastApiBaseUrl() {
        String value = fastApiBaseUrl == null ? "" : fastApiBaseUrl.trim();
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    static String normalizeRepositoryUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub 저장소 주소를 입력해 주세요.");
        }
        try {
            URI uri = URI.create(rawUrl.trim());
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || uri.getUserInfo() != null
                    || uri.getQuery() != null
                    || uri.getFragment() != null
                    || !(uri.getPort() == -1 || uri.getPort() == 443)
                    || !"github.com".equalsIgnoreCase(uri.getHost())) {
                throw new IllegalArgumentException("https://github.com/owner/repository 형식만 허용됩니다.");
            }
            String[] segments = uri.getPath().replaceFirst("^/+", "").split("/");
            if (segments.length != 2) {
                throw new IllegalArgumentException("GitHub owner/repository 주소를 입력해 주세요.");
            }
            String owner = segments[0];
            String repository = segments[1].replaceFirst("(?i)\\.git$", "");
            if (!REPOSITORY_SEGMENT.matcher(owner).matches()
                    || !REPOSITORY_SEGMENT.matcher(repository).matches()) {
                throw new IllegalArgumentException("GitHub 저장소 주소 형식이 올바르지 않습니다.");
            }
            return "https://github.com/" + owner + "/" + repository;
        } catch (IllegalArgumentException error) {
            if (error.getMessage() != null && error.getMessage().startsWith("GitHub")) {
                throw error;
            }
            throw new IllegalArgumentException(
                "https://github.com/owner/repository 형식만 허용됩니다.",
                error
            );
        }
    }
}
