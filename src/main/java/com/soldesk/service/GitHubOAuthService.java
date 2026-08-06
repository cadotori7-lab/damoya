package com.soldesk.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GitHubOAuthService {

    private static final String TOKEN = "githubAccessToken";
    private static final String LOGIN = "githubLogin";
    private static final String STATE = "githubOAuthState";
    private static final String VERIFIER = "githubOAuthVerifier";

    private final RestTemplate restTemplate = new RestTemplate();
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${github.oauth.client-id:}")
    private String clientId;

    @Value("${github.oauth.client-secret:}")
    private String clientSecret;

    @Value("${github.oauth.callback-url:http://localhost:8080/oauth/git/callback}")
    private String callbackUrl;

    public String buildAuthorizationUrl(HttpSession session) {
        requireConfiguration();

        String state = randomValue();
        String verifier = randomValue();
        session.setAttribute(STATE, state);
        session.setAttribute(VERIFIER, verifier);

        return UriComponentsBuilder
            .fromHttpUrl("https://github.com/login/oauth/authorize")
            .queryParam("client_id", clientId.trim())
            .queryParam("redirect_uri", callbackUrl.trim())
            .queryParam("scope", "repo")
            .queryParam("state", state)
            .queryParam("code_challenge", sha256(verifier))
            .queryParam("code_challenge_method", "S256")
            .build()
            .encode()
            .toUriString();
    }

    public void finishAuthorization(String code, String state, HttpSession session) {
        String expectedState = (String) session.getAttribute(STATE);
        String verifier = (String) session.getAttribute(VERIFIER);
        session.removeAttribute(STATE);
        session.removeAttribute(VERIFIER);

        if (code == null || code.isBlank() || expectedState == null || verifier == null
                || !MessageDigest.isEqual(
                    expectedState.getBytes(StandardCharsets.UTF_8),
                    String.valueOf(state).getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("GitHub 로그인 요청을 확인할 수 없습니다. 다시 시도해 주세요.");
        }

        String token = exchangeCode(code, verifier);
        Map<String, Object> user = getJson(
            "https://api.github.com/user",
            token,
            new ParameterizedTypeReference<Map<String, Object>>() { }
        );
        String login = String.valueOf(user.getOrDefault("login", ""));
        if (login.isBlank()) {
            throw new IllegalStateException("GitHub 사용자 정보를 읽을 수 없습니다.");
        }

        session.setAttribute(TOKEN, token);
        session.setAttribute(LOGIN, login);
    }

    public List<RepositoryOption> listRepositories(HttpSession session) {
        String token = getAccessToken(session);
        if (token == null) {
            return List.of();
        }

        String url = UriComponentsBuilder
            .fromHttpUrl("https://api.github.com/user/repos")
            .queryParam("visibility", "all")
            .queryParam("affiliation", "owner,collaborator,organization_member")
            .queryParam("sort", "updated")
            .queryParam("per_page", 100)
            .build()
            .encode()
            .toUriString();
        List<Map<String, Object>> repositories = getJson(
            url,
            token,
            new ParameterizedTypeReference<List<Map<String, Object>>>() { }
        );

        List<RepositoryOption> result = new ArrayList<>();
        for (Map<String, Object> repository : repositories) {
            String name = String.valueOf(repository.getOrDefault("full_name", ""));
            String repositoryUrl = String.valueOf(repository.getOrDefault("html_url", ""));
            if (!name.isBlank() && !repositoryUrl.isBlank()) {
                result.add(new RepositoryOption(
                    name,
                    repositoryUrl,
                    Boolean.TRUE.equals(repository.get("private"))
                ));
            }
        }
        return result;
    }

    public String getAccessToken(HttpSession session) {
        Object token = session.getAttribute(TOKEN);
        return token instanceof String && !((String) token).isBlank() ? (String) token : null;
    }

    public String getLogin(HttpSession session) {
        Object login = session.getAttribute(LOGIN);
        return login instanceof String ? (String) login : "";
    }

    public void disconnect(HttpSession session) {
        session.removeAttribute(TOKEN);
        session.removeAttribute(LOGIN);
        session.removeAttribute(STATE);
        session.removeAttribute(VERIFIER);
    }

    private String exchangeCode(String code, String verifier) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId.trim());
        body.add("client_secret", clientSecret.trim());
        body.add("code", code);
        body.add("redirect_uri", callbackUrl.trim());
        body.add("code_verifier", verifier);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            "https://github.com/login/oauth/access_token",
            HttpMethod.POST,
            new HttpEntity<>(body, headers),
            new ParameterizedTypeReference<Map<String, Object>>() { }
        );
        Map<String, Object> payload = response.getBody();
        String token = payload == null ? "" : String.valueOf(payload.getOrDefault("access_token", ""));
        if (token.isBlank()) {
            throw new IllegalStateException("GitHub 액세스 토큰을 발급받지 못했습니다.");
        }
        return token;
    }

    private <T> T getJson(String url, String token, ParameterizedTypeReference<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        ResponseEntity<T> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            type
        );
        T body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("GitHub 응답이 비어 있습니다.");
        }
        return body;
    }

    private void requireConfiguration() {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException("GitHub OAuth 환경변수가 설정되지 않았습니다.");
        }
    }

    private String randomValue() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", error);
        }
    }

    public static class RepositoryOption {
        private final String name;
        private final String url;
        private final boolean privateRepository;

        public RepositoryOption(String name, String url, boolean privateRepository) {
            this.name = name;
            this.url = url;
            this.privateRepository = privateRepository;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public boolean isPrivateRepository() {
            return privateRepository;
        }
    }
}
