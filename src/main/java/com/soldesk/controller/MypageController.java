package com.soldesk.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.service.MemberService;
import com.soldesk.service.UnivService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.UnivVO;


@Controller
@RequestMapping("/mypage")
public class MypageController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UnivService univService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/index")
    public String index(Model model) {
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberVO member = memberService.findByLoginId(member_id);
        UnivVO univ = univService.getUnivByDeptId(member.getDept_id()); // 학생의 대학 이름과 학과 이름 조회
        model.addAttribute("member", member);
        model.addAttribute("univList", univService.getAllUniv());
        model.addAttribute("univ", univ);

        return "mypage/index";
    }

    @PostMapping("/update")
    public String update(MemberVO member) {
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();
        member.setLogin_id(member_id); // 로그인한 사용자의 ID를 설정
        memberService.updateMember(member); // 회원 정보 업데이트
        return "redirect:/mypage/index"; // 업데이트 후 마이페이지로 리다이렉트
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("password") String password,
                           HttpServletRequest request,
                           Model model) {
        // 현재 로그인 사용자
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberVO member = memberService.findByLoginId(member_id);
 
        // 비밀번호 재확인 (모달에서 입력받은 값)
        String storedHash = member.getPassword();
        if (storedHash == null || !passwordEncoder.matches(password, storedHash)) {
            // 실패 시 마이페이지 데이터를 다시 채워서 모달을 자동으로 다시 열어줌
            UnivVO univ = univService.getUnivByDeptId(member.getDept_id());
            model.addAttribute("member", member);
            model.addAttribute("univList", univService.getAllUniv());
            model.addAttribute("univ", univ);
            model.addAttribute("withdrawError", "비밀번호가 일치하지 않아요.");
            model.addAttribute("openWithdraw", true); // JSP에서 모달 자동 오픈용
            return "mypage/index";
        }

        // soft delete + 팀장 승계(아직 미구현)
        memberService.withdraw(member_id);

        // 로그아웃 처리: 세션 무효화 + 인증정보 제거
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam("currentPassword")    String currentPassword,
                                 @RequestParam("newPassword")        String newPassword,
                                 @RequestParam("newPasswordConfirm") String newPasswordConfirm,
                                 RedirectAttributes ra,
                                 Model model) {
 
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginId = auth.getName();
        MemberVO member = memberService.findByLoginId(loginId);
        // 소셜 회원 차단
        if (member == null || !"LOCAL".equals(member.getProvider())) {
            return backWithError(ra, "소셜 로그인 계정은 비밀번호를 변경할 수 없어요.");
        }
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            return backWithError(ra, "현재 비밀번호가 일치하지 않아요.");
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            return backWithError(ra, "새 비밀번호가 서로 일치하지 않아요.");
        }
        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            return backWithError(ra, "현재와 다른 비밀번호를 입력해주세요.");
        }
        if (newPassword.length() < 8) {
            return backWithError(ra, "비밀번호는 8자 이상이어야 해요.");
        }
        memberService.updatePassword(member.getMember_id(), passwordEncoder.encode(newPassword));
        ra.addFlashAttribute("passwordSuccess", "비밀번호가 변경됐어요.");
        return "redirect:/mypage/index";
    }

    private String backWithError(RedirectAttributes ra, String message) {
        ra.addFlashAttribute("passwordError", message);
        ra.addFlashAttribute("openPassword", true);
        return "redirect:/mypage/index";
    }
}
