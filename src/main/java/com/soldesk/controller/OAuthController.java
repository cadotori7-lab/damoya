package com.soldesk.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.service.GitHubOAuthService;
import com.soldesk.service.OAuthService;

@Controller
@RequestMapping("/oauth")
public class OAuthController {

    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);
    private final OAuthService oAuthService;
    private final GitHubOAuthService gitHubOAuthService;

    public OAuthController(OAuthService oAuthService, GitHubOAuthService gitHubOAuthService) {
        this.oAuthService = oAuthService;
        this.gitHubOAuthService = gitHubOAuthService;
    }
    
    // OAuth - 구글 OAuth 서버 요청
    @GetMapping("/google")
    public void googleLogin(HttpServletResponse response) throws IOException{
        response.sendRedirect(oAuthService.getGooleAuthUrl());
    }
    // OAuth - 구글 콜백
    @GetMapping("/google/callback")
    public String gooleCallback(@RequestParam String code, 
                                HttpServletRequest request,
                                HttpServletResponse response) 
                                throws IOException, InterruptedException{
            
            oAuthService.processGoolge(code, request, response);

        return "redirect:/";
    }

    @GetMapping("/git")
    public String gitConnect(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            return "redirect:" + gitHubOAuthService.buildAuthorizationUrl(session);
        } catch (RuntimeException error) {
            logger.warn("GitHub OAuth 시작 실패: {}", error.getMessage());
            redirectAttributes.addFlashAttribute("githubError", error.getMessage());
            return "redirect:/gitTest/";
        }
    }

    @GetMapping("/git/callback")
    public String gitCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            gitHubOAuthService.finishAuthorization(code, state, session);
        } catch (RuntimeException error) {
            logger.warn("GitHub OAuth 완료 실패: {}", error.getMessage());
            redirectAttributes.addFlashAttribute("githubError", error.getMessage());
        }
        return "redirect:/gitTest/";
    }

    @PostMapping("/git/disconnect")
    public String gitDisconnect(HttpSession session) {
        gitHubOAuthService.disconnect(session);
        return "redirect:/gitTest/";
    }

}
