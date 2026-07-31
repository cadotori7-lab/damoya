package com.soldesk.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/test")
public class TestController {

    private final Logger logger = Logger.getLogger(TestController.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();

    // FastAPI 서비스 주소 (app.properties 의 fastapi.base-url, 환경변수 FASTAPI_BASE_URL 로 오버라이드 가능)
    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    @GetMapping("/")
    public String mentorHome() {
        logger.info("mentorHome");
        return "test/home";
    }

    @GetMapping("/chat")
    public String chat() {
        // 대화 기록은 FastAPI 에이전트가 세션 ID 기준으로 관리한다.
        return "test/chat";
    }

    @PostMapping(
        value = "/chat/api",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>> chatApi(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        // 프론트는 message 키로 보내지만 question 키도 함께 허용한다.
        String question = request.getOrDefault("question", request.getOrDefault("message", "")).trim();
        if (question.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("detail", "질문을 입력해 주세요.")
            );
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new java.util.HashMap<>();
            body.put("question", question);
            // 브라우저 세션 ID로 에이전트의 대화 기록을 구분한다.
            body.put("sessionId", session.getId());

            HttpEntity<Map<String, Object>> fastApiRequest = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                fastApiBaseUrl + "/chat",
                fastApiRequest,
                Map.class
            );

            return ResponseEntity.ok(response == null ? Map.of() : response);
        } catch (HttpStatusCodeException e) {
            logger.warning("FastAPI 챗봇 오류: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(
                Map.of("detail", "챗봇 요청이 실패했습니다: " + e.getResponseBodyAsString())
            );
        } catch (ResourceAccessException e) {
            logger.warning("FastAPI 챗봇 연결 실패: " + e.getMessage());
            return ResponseEntity.status(503).body(
                Map.of("detail", "Python 챗봇 서버에 연결할 수 없습니다. localhost:8001 서버를 확인하세요.")
            );
        }
    }

    @GetMapping("/mentor/register")
    public String mentorRegister() {
        logger.info("멘토 등록 페이지 접근");
        return "test/register";
    }

    @PostMapping("/mentor/register")
    public String mentorRegisterSubmit(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file,
            Model model) {
        logger.info("멘토 등록 요청 name=" + name + ", file=" + file.getOriginalFilename());
        model.addAttribute("name", name);

        try {
            Map<String, Object> result = verifyWithFastApi(name, file);
            model.addAttribute("result", result);
        } catch (IOException e) {
            logger.severe("파일 처리 실패: " + e.getMessage());
            model.addAttribute("error", "파일 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("FastAPI 호출 실패: " + e.getMessage());
            model.addAttribute("error", "OCR 검증 서버 호출에 실패했습니다: " + e.getMessage());
        }

        return "test/result";
    }

    // FastAPI OCR 검증 서비스 호출
    @SuppressWarnings("unchecked")
    private Map<String, Object> verifyWithFastApi(String name, MultipartFile file) throws IOException {
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", name);
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(fastApiBaseUrl + "/verify", request, Map.class);
    }
    // 프로젝트 등록(임의)
    @GetMapping("/project/register")
    public String projectRegister(Model model) {
        logger.info("프로젝트 등록 페이지 접근");
        model.addAttribute("reference", UUID.randomUUID().toString());
        return "test/project/register";
    }

    // MCP 멘토 매칭용 더미 프로젝트 정보 수신 및 FastAPI 호출
    @PostMapping(
        value = "/project/register",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public String projectRegisterSubmit(
            @RequestParam("reference") String reference,
            @RequestParam("projectName") String projectName,
            @RequestParam("projectDescription") String projectDescription,
            Model model) {
        String normalizedReference = reference == null || reference.isBlank()
            ? UUID.randomUUID().toString()
            : reference.trim();
        String normalizedName = projectName == null ? "" : projectName.trim();
        String normalizedDescription = projectDescription == null
            ? ""
            : projectDescription.trim();

        model.addAttribute("submitted", true);
        model.addAttribute("reference", normalizedReference);
        model.addAttribute("projectName", normalizedName);
        model.addAttribute("projectDescription", normalizedDescription);

        logger.info(
            "더미 프로젝트 멘토 매칭 요청 reference="
                + normalizedReference
                + ", projectName="
                + normalizedName
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new java.util.HashMap<>();
            body.put("reference", normalizedReference);
            body.put("projectName", normalizedName);
            body.put("projectDescription", normalizedDescription);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = restTemplate.postForObject(
                fastApiBaseUrl + "/mentor-match",
                request,
                Map.class
            );
            model.addAttribute("matchResult", result);
        } catch (HttpStatusCodeException e) {
            logger.warning("FastAPI 멘토 추천 오류: " + e.getResponseBodyAsString());
            model.addAttribute(
                "error",
                "멘토 추천 요청이 실패했습니다: " + e.getResponseBodyAsString()
            );
        } catch (ResourceAccessException e) {
            logger.warning("FastAPI 멘토 추천 연결 실패: " + e.getMessage());
            model.addAttribute(
                "error",
                "Python 서버에 연결할 수 없습니다. localhost:8001 서버를 확인하세요."
            );
        }

        return "test/project/register";
    }

    // Git 프로젝트 AI 리포트 테스트 페이지
    @GetMapping("/git-report")
    public String gitReport(Model model) {
        model.addAttribute("projectName", "다모여");
        model.addAttribute(
            "repositoryUrl",
            "https://github.com/cadotori7-lab/damoya"
        );
        model.addAttribute("ref", "main");
        return "test/git-report";
    }

    // Git 주소를 FastAPI MCP 리포트 서비스에 전달
    @PostMapping(
        value = "/git-report",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public String gitReportSubmit(
            @RequestParam("projectName") String projectName,
            @RequestParam("repositoryUrl") String repositoryUrl,
            @RequestParam(value = "ref", required = false) String ref,
            Model model) {
        String normalizedName = projectName == null ? "" : projectName.trim();
        String normalizedUrl = repositoryUrl == null ? "" : repositoryUrl.trim();
        String normalizedRef = ref == null ? "" : ref.trim();

        model.addAttribute("submitted", true);
        model.addAttribute("projectName", normalizedName);
        model.addAttribute("repositoryUrl", normalizedUrl);
        model.addAttribute("ref", normalizedRef);

        if (normalizedName.isEmpty() || normalizedUrl.isEmpty()) {
            model.addAttribute("error", "프로젝트 이름과 Git 저장소 주소를 입력해 주세요.");
            return "test/git-report";
        }

        logger.info(
            "Git AI 리포트 테스트 요청 projectName="
                + normalizedName
                + ", repositoryUrl="
                + normalizedUrl
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new java.util.HashMap<>();
            body.put("projectName", normalizedName);
            body.put("repositoryUrl", normalizedUrl);
            if (!normalizedRef.isEmpty()) {
                body.put("ref", normalizedRef);
            }

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = restTemplate.postForObject(
                fastApiBaseUrl + "/git-report",
                request,
                Map.class
            );
            model.addAttribute("reportResult", result);
        } catch (HttpStatusCodeException e) {
            logger.warning("FastAPI Git 리포트 오류: " + e.getResponseBodyAsString());
            model.addAttribute(
                "error",
                "AI 리포트 요청이 실패했습니다: " + e.getResponseBodyAsString()
            );
        } catch (ResourceAccessException e) {
            logger.warning("FastAPI Git 리포트 연결 실패: " + e.getMessage());
            model.addAttribute(
                "error",
                "Python 서버에 연결할 수 없습니다. localhost:8501 서버를 확인하세요."
            );
        } catch (Exception e) {
            logger.severe("Git AI 리포트 처리 실패: " + e.getMessage());
            model.addAttribute(
                "error",
                "AI 리포트 처리 중 오류가 발생했습니다: " + e.getMessage()
            );
        }

        return "test/git-report";
    }

    // FastAPI가 만든 PDF를 Spring 경유로 다운로드
    @GetMapping("/git-report/{reportId}/pdf")
    public ResponseEntity<byte[]> downloadGitReport(
            @PathVariable("reportId") String reportId) {
        if (
            reportId == null
            || !reportId.matches("[a-z0-9][a-z0-9-]{7,100}")
        ) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(
                fastApiBaseUrl + "/git-report/" + reportId + "/pdf",
                byte[].class
            );
            byte[] content = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || content == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                org.springframework.http.ContentDisposition
                    .attachment()
                    .filename(reportId + ".pdf", StandardCharsets.UTF_8)
                    .build()
            );
            headers.setCacheControl("private, no-store");
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (HttpStatusCodeException e) {
            logger.warning("FastAPI PDF 다운로드 오류: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (ResourceAccessException e) {
            logger.warning("FastAPI PDF 다운로드 연결 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
