package com.soldesk.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk.service.OAuthService;

@Controller
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;
    
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


}
