package com.soldesk.controller;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/gitTest")
public class GitTestRestController {

    // 리포트 ID 패턴 정규식
    private static final Pattern REPORT_ID_PATTERN = Pattern.compile(
        "^[a-z0-9][a-z0-9-]{7,100}$"
    );

    private final RestTemplate restTemplate = new RestTemplate();


    @Value("${fastapi.base-url:http://localhost:8501}")
    private String fastApiBaseUrl;

    // 리포트 PDF 다운로드
    @GetMapping(value = "/reports/{reportId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadReport(@PathVariable("reportId") String reportId) {
        // 리포트 ID 패턴 검사
        if (!REPORT_ID_PATTERN.matcher(reportId).matches()) {
            return ResponseEntity.badRequest().body(
                "올바르지 않은 리포트 ID입니다.".getBytes(StandardCharsets.UTF_8)
            );
        }

        try {
            // FastAPI에 리포트 PDF 다운로드 요청
            ResponseEntity<byte[]> response = restTemplate.getForEntity(
                normalizeFastApiBaseUrl() + "/git-report/" + reportId + "/pdf",
                byte[].class
            );
            // 리포트 PDF 내용 반환
            byte[] content = response.getBody();
            // 리포트 PDF 내용이 없으면 404 반환
            if (content == null || content.length == 0) {
                return ResponseEntity.notFound().build();
            }
            // 리포트 PDF 내용 반환
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + reportId + ".pdf\""
                )
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(content);
        } catch (HttpStatusCodeException error) {
            return ResponseEntity.status(error.getStatusCode()).build();
        } catch (ResourceAccessException error) {
            return ResponseEntity.status(503).build();
        }
    }

    // FastAPI 기본 URL 정규화
    private String normalizeFastApiBaseUrl() {
        String value = fastApiBaseUrl == null ? "" : fastApiBaseUrl.trim();
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
