package com.soldesk.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soldesk.mapper.MemberMapper;
import com.soldesk.security.MemberDetailsService;
import com.soldesk.vo.MemberVO;

@Service
public class OAuthService {

    private final PasswordEncoder passwordEncoder;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private MemberDetailsService memberUserDetailsService;

    private final static Logger log = LoggerFactory.getLogger(OAuthService.class);

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    OAuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    //구글 로그인 URL
    public String getGooleAuthUrl(){
        return "https://accounts.google.com/o/oauth2/v2/auth"
            + "?client_id=" + googleClientId
            + "&redirect_uri=" + URLEncoder.encode(googleRedirectUri, StandardCharsets.UTF_8)
            + "&response_type=code"
            + "&scope=" + URLEncoder.encode("email profile", StandardCharsets.UTF_8)
            + "&access_type=online";
    }
    //인증 처리
    public void processGoolge(String code, 
        HttpServletRequest request, HttpServletResponse response)
        throws IOException, InterruptedException{

        Map<String, String> tokenParams = Map.of(
            "code", code,
            "client_id", googleClientId,
            "client_secret", googleClientSecret,
            "redirect_uri", googleRedirectUri,
            "grant_type", "authorization_code"
        );
        StringBuilder tokenReqBody = new StringBuilder();
        for(Map.Entry<String, String> e : tokenParams.entrySet()){
            if(tokenReqBody.length() > 0) tokenReqBody.append('&');
            tokenReqBody.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                .append('=')
                .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }
        // 구글 토큰 엔드포인트로 POST 요청 전송
        HttpRequest tokenReq = HttpRequest.newBuilder()
            .uri(URI.create("https://oauth2.googleapis.com/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(tokenReqBody.toString()))
            .build();
        String tokenBody = httpClient.send(tokenReq, HttpResponse.BodyHandlers.ofString()).body();
        log.debug(tokenBody);

        // 엑세스 토큰 파싱
        JsonNode tokenJson = objectMapper.readTree(tokenBody);
        String accessToken = tokenJson.get("access_token").asText();

        // 엑세스 토큰으로 구글 사용자 정보 호출(로그인한 사용자의 고유id, 이메일, 이름 등의 프로필 정보를 가져오기)
        HttpRequest userReq = HttpRequest.newBuilder()
            .uri(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"))
            .header("Authorization", "Bearer " + accessToken)
            .GET().build();
        JsonNode userJson = objectMapper.readTree(
            httpClient.send(userReq, HttpResponse.BodyHandlers.ofString()).body());
        
        String providerId = userJson.get("id").asText();//회원을 식별할 수 있는 값
        String email = userJson.get("email").asText();
        String name 
            = userJson.has("name") ? userJson.get("name").asText() : "구글회원";

        // DB에 회원 저장
        loginOrJoin("GOOGLE", providerId, email, name, request, response);

    }
    private void loginOrJoin(String provider, String providerId, String email, String name,
        HttpServletRequest request, HttpServletResponse response){

        //OAuth로 로그인한 회원이 기존회원인지 아닌지 검사
        MemberVO member = memberMapper.findByProviderAndProviderId(provider, providerId);

        // 로컬로 가입된 회원인지 검사
        if(member == null){
            member = memberMapper.findByEmail(email);
        }
        // 로컬로 가입된 회원이 아니면 DB에 새롭게 회원 정보를 저장
        if(member == null){
            member = new MemberVO();
            member.setLogin_id(provider.toLowerCase() + "_" + providerId);
            member.setEmail(email);
            member.setName(name);
            member.setProvider(provider);
            member.setProvider_id(providerId);
            member.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            memberMapper.insertOAuthMember(member);
        }

        UserDetails userDetails =
             memberUserDetailsService.loadUserByUsername(member.getEmail());
        
        // oAuth 유저는 비밀번호 검증없이 로그인
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(userDetails, 
                                       null, 
                                                    userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // 시큐리티 세션에 인증된 사용자 등록
        new HttpSessionSecurityContextRepository().saveContext(SecurityContextHolder.getContext(), 
            request, response);
           
    }

}
