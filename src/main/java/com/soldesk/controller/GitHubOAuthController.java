package com.soldesk.controller;

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

@Controller
@RequestMapping("/gitTest/github")
public class GitHubOAuthController {

    private static final Logger logger = LoggerFactory.getLogger(GitHubOAuthController.class);
    private final GitHubOAuthService gitHubOAuthService;

    public GitHubOAuthController(GitHubOAuthService gitHubOAuthService) {
        this.gitHubOAuthService = gitHubOAuthService;
    }

    @GetMapping("/connect")
    public String connect(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            return "redirect:" + gitHubOAuthService.buildAuthorizationUrl(session);
        } catch (RuntimeException error) {
            logger.warn("GitHub OAuth 시작 실패: {}", error.getMessage());
            redirectAttributes.addFlashAttribute("githubError", error.getMessage());
            return "redirect:/gitTest/";
        }
    }

    @GetMapping("/callback")
    public String callback(
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

    @PostMapping("/disconnect")
    public String disconnect(HttpSession session) {
        gitHubOAuthService.disconnect(session);
        return "redirect:/gitTest/";
    }
}