package com.soldesk.controller;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/test")
public class TestController {

    private final Logger logger = Logger.getLogger(TestController.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();

    // FastAPI OCR 검증 서비스 주소
    private static final String FASTAPI_VERIFY_URL = "http://localhost:8000/verify";

    @GetMapping("/")
    public String mentorHome() {
        logger.info("mentorHome");
        return "test/home";
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
