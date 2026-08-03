package com.soldesk.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

// 이름과 자격증 이미지를 파이썬 FastAPI(/verify) 로 보내 OCR 대조 결과를 받아오기
@Service
public class CertVerifyService {

    // FastAPI 서비스 주소 (app.properties 의 fastapi.base-url)
    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public Map<String, Object> verify(String name, MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 업로드된 파일을 파일명이 유지되도록 감싸서 전송
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

    //이름과 자격증 이미지가 일치하는지 여부만 반환
    public boolean isMatched(String name, MultipartFile file) throws IOException {
        Map<String, Object> result = verify(name, file);
        return result != null && Boolean.TRUE.equals(result.get("matched"));
    }
}

