package com.soldesk.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
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

    // FastAPI OCR 검증 서비스 주소
    private static final String FASTAPI_VERIFY_URL = "http://localhost:8000/verify";
    private static final String FASTAPI_CHAT_URL = "http://localhost:8000/chat";

    @GetMapping("/")
    public String mentorHome() {
        logger.info("mentorHome");
        return "test/home";
    }

    @GetMapping("/chat")
    public String chat(HttpSession session) {
        // 채팅 페이지 진입 시 메뉴부터 시작
        session.setAttribute("chatStep", "menu");
        session.setAttribute("chatHistory", new ArrayList<String>());
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
        String message = request.getOrDefault("message", "").trim();
        String requestedStep = request.getOrDefault("step", "").trim();
        Object sessionStep = session.getAttribute("chatStep");
        String step = sessionStep == null ? "menu" : String.valueOf(sessionStep);

        @SuppressWarnings("unchecked")
        List<String> history = (List<String>) session.getAttribute("chatHistory");
        if (history == null) {
            history = new ArrayList<>();
            session.setAttribute("chatHistory", history);
        }

        // 페이지의 '처음부터' 버튼은 빈 메시지와 menu 단계로 초기화를 요청한다.
        if (message.isEmpty() && "menu".equals(requestedStep)) {
            step = "menu";
            history.clear();
            session.setAttribute("chatStep", step);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new java.util.HashMap<>();
            body.put("message", message);
            body.put("step", step);
            body.put("history", new ArrayList<>(history));

            HttpEntity<Map<String, Object>> fastApiRequest = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                FASTAPI_CHAT_URL,
                fastApiRequest,
                Map.class
            );

            if (response != null && response.get("step") != null) {
                session.setAttribute("chatStep", String.valueOf(response.get("step")));
            }

            if (!message.isEmpty() && response != null && response.get("reply") != null) {
                history.add("사용자: " + message);
                history.add("AI: " + String.valueOf(response.get("reply")));
                while (history.size() > 12) {
                    history.remove(0);
                }
            }

            return ResponseEntity.ok(response == null ? Map.of() : response);
        } catch (HttpStatusCodeException e) {
            logger.warning("FastAPI 챗봇 오류: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(
                Map.of("detail", "챗봇 요청이 실패했습니다: " + e.getResponseBodyAsString())
            );
        } catch (ResourceAccessException e) {
            logger.warning("FastAPI 챗봇 연결 실패: " + e.getMessage());
            return ResponseEntity.status(503).body(
                Map.of("detail", "Python 챗봇 서버에 연결할 수 없습니다. localhost:8000 서버를 확인하세요.")
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
        return restTemplate.postForObject(FASTAPI_VERIFY_URL, request, Map.class);
    }
}
