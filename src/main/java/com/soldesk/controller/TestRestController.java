package com.soldesk.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/test")
public class TestRestController {
    private final Logger logger = LoggerFactory.getLogger(TestRestController.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // 프로젝트 멘토 매칭 JSON 요청
    @PostMapping("/project/register/api")
    public ResponseEntity<?> projectRegister(@RequestBody Map<String, String> request) {
        logger.info("프로젝트 멘토 매칭 JSON 요청 수신");
        return restTemplate.postForEntity("http://localhost:8001/mentor-match", request, String.class);
    }
}
