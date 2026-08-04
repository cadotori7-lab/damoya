package com.soldesk.controller;

import java.security.Principal; // ★ Principal 임포트 추가
import java.util.List;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.service.MemberService;
import com.soldesk.service.ParticipationService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.ParticipationVO;

@Controller
@RequestMapping("/projects")
public class ParticipationController {
    
    @Autowired 
    private ParticipationService participationService; 

    @Autowired
    private MemberService memberService; // ★ 회원 정보 조회를 위해 추가

    // 지원서 제출 처리
    @PostMapping("/{projectId}/apply")
    public String submitApplication(@PathVariable("projectId") Long projectId,
                                    ParticipationVO participationVO,
                                    Principal principal, // ★ 스프링 시큐리티가 인증된 유저 정보를 넣어줍니다.
                                    RedirectAttributes rttr) {
        
        // 1. 로그인을 하지 않은 경우
        if (principal == null) {
            rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
            return "redirect:/auth/login"; // ★ 올바른 로그인 주소(/auth/login)로 수정!
        }

        // 2. 현재 로그인한 아이디(login_id)로 회원 정보를 통째로 조회
        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);

        if (loginUser == null) {
            rttr.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        }

        // 3. VO에 프로젝트 ID와 회원 고유 번호(member_id) 세팅
        Long loginMemberId = (long) loginUser.getMember_id();
        
        participationVO.setProjectId(projectId);
        participationVO.setMemberId(loginMemberId);

        try {
                // ★ 서비스 호출 (여기서 중복이면 IllegalStateException 발생)
                participationService.applyForProject(participationVO);
                rttr.addFlashAttribute("msg", "지원서가 성공적으로 제출되었습니다.");
            } catch (IllegalStateException e) {
                // ★ 중복 지원 등의 이유로 예외가 발생하면 메시지를 띄우고 상세 페이지로 복귀
                rttr.addFlashAttribute("msg", e.getMessage());
            }
       return "redirect:/project/detail?id=" + projectId; 
    }

    // 지원자 목록 보기
    @GetMapping("/{projectId}/applicants")
    public String viewApplicants(@PathVariable("projectId") Long projectId,
                                 Model model,
                                 HttpSession session) {
        List<ParticipationVO> applicantList = participationService.getApplicants(projectId);
        model.addAttribute("applicantsList", applicantList);                                
        
        return "project/applicant_list"; 
    }
}