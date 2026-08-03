package com.soldesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soldesk.service.MemberService;
import com.soldesk.vo.PublicProfileVO;

/**
 * 공개 프로필 조회 API.
 * 프로필 보기 모달이 Ajax(fetch)로 호출한다.
 *
 *   GET /members/{memberId}/profile  →  JSON (공개 필드만)
 *
 * 서버가 공개 가능한 필드만 골라 내려주는 것이 핵심.
 * 비공개(profile_public=false) 회원은 이름·배지 정도만 채워 반환한다.
 */
@Controller
public class PublicProfileController {

    @Autowired private MemberService memberService;

    @GetMapping("/members/{memberId}/profile")
    @ResponseBody
    public ResponseEntity<PublicProfileVO> profile(@PathVariable("memberId") int memberId) {

        PublicProfileVO profile = memberService.findPublicProfile(memberId);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }
}
