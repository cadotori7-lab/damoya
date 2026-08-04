package com.soldesk.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.service.CommentService;
import com.soldesk.service.MemberService;
import com.soldesk.service.ParticipationService;
import com.soldesk.service.ReportService;
import com.soldesk.service.TalentService;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.PageBean;
import com.soldesk.vo.ParticipationVO;
import com.soldesk.vo.ProjectVO;
import com.soldesk.vo.TalentVO;

@Controller
@RequestMapping("/talent")
public class TalentController {
    
    private final RestClient restClient;

    @Autowired 
    private TalentService talentService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private ReportService reportService;


    TalentController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long id, 
                        Model model, Principal principal){
    
        
        // 해당 게시글 데이터 가져오기
        TalentVO talent = talentService.getTalentById(id);

        // 작성자 본인이 맞는지 확인
        boolean isOwner = false;
        MemberVO loginUser = null;
        List<ProjectVO> leaderProjects = null;

        if(principal != null ){
            String loginId = principal.getName();
            loginUser = memberService.findByLoginId(loginId);

                if (loginUser != null) {
                // 로그인한 유저와 게시글 작성자 비교
                if (talent.getMemberId() == loginUser.getMember_id()) {
                    isOwner = true;
                }
                // 내가 팀장인 프로젝트 목록 가져오기
                leaderProjects = talentService.getLeaderProjectsByMemberId((long) loginUser.getMember_id());
            }
        }
        model.addAttribute("leaderProjects", leaderProjects);
        model.addAttribute("talent", talent);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("member", loginUser);
        
        return "talent/detail";
    }
    // 함께하기 제의
    @PostMapping("/offer/send")
    public String sendOffer(ParticipationVO participationVO,
                            @RequestParam("postId") Long postId,
                            Principal principal,
                            RedirectAttributes rttr) {
        //로그인 체크
        if(principal == null){
            rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
            return "redirect:/auth/login";
        }

        // 회원 정보 조회
        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);

        if(loginUser == null){
            rttr.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        }

        // Offer로 고정 / 역할은 Member로
        participationVO.setJoinStatus("OFFER");
        participationVO.setProjectRole("Member");
        
        try {
            talentService.insertOffer(participationVO);
            rttr.addFlashAttribute("msg", "성공적으로 제의를 보냈습니다!");

        } catch (Exception e) {
            rttr.addFlashAttribute("msg", "제의를 보내는 중에 오류가 발생했습니다.");
        }

        return "redirect:/talent/detail?id=" + postId;
    }

    @GetMapping("/form")
    public String form() {
        return "talent/form";
    }

    // 인재풀 등록
    @PostMapping("/register")
    public String registerTalent(@ModelAttribute TalentVO talentVO,
                               RedirectAttributes rrtr,
                               Principal principal){
        
        if(principal == null){
            return "redirect:/auth/login";
        }
        
        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);

        if(loginUser == null){
            return "redirect:/auth/login";
        } 

        // 다중 체크된 카테고리 리스트를 콤마(,)로 연결하여 문자열로 변환
        if (talentVO.getCategoryList() != null && !talentVO.getCategoryList().isEmpty()) {
            talentVO.setCategory(String.join(",", talentVO.getCategoryList()));
        }

        Long loginMemberId = (long) loginUser.getMember_id();

        talentService.registerTalent(talentVO, loginMemberId);

        rrtr.addFlashAttribute("msg", "게시글이 성공적으로 등록되었습니다!");
        return "redirect:/talent/list";
    }

    @GetMapping("/list")
    public String getTalentList(
            @ModelAttribute TalentVO vo,
            Model model, Principal principal) {
            
            if (vo.getKind() == null || vo.getKind().trim().isEmpty() || vo.getKind().equals("all")) {
                vo.setKind(null);
            }
            
            vo.setAmount(6);
            
            // 페이지가 1 미만으로 내려가지 않게 처리
            if(vo.getPage() < 1){
                vo.setPage(1);
            }
            // 페이징 쿼리를 위한 시작 위치(offset) 계산
            vo.setOffset((vo.getPage() - 1) * vo.getAmount());
            
            // DB에서 전체 개수와 리스트 가져오기
            int totalPost = talentService.getTotalCount(vo);
            PageBean pageBean = new PageBean(vo.getPage(), totalPost, vo.getAmount());
            List<TalentVO> talentList = talentService.getTalentList(vo);

            model.addAttribute("talentList", talentList);
            model.addAttribute("pageBean", pageBean);
            model.addAttribute("currentSort", vo.getSort() != null ? vo.getSort() : "latest");

            return "talent/list";
    }

    // 수정 페이지 
    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long postId,
                           Model model,
                           Principal principal,
                           RedirectAttributes rttr){
    //로그인 확인
    if(principal == null){
        return "redirect:/auth/login";
    }

    TalentVO talent = talentService.getTalentById(postId);
    MemberVO loginUser = memberService.findByLoginId(principal.getName());
    long loginMemberId = loginUser.getMember_id();

    // 작성자 본인이 맞는지 확인
    if (talent.getMemberId() != loginMemberId) {
        rttr.addFlashAttribute("msg", "권한이 없습니다.");
        return "redirect:/talent/detail?id=" + postId;
    }
    model.addAttribute("talent", talent);
    model.addAttribute("mode", "update");
     return "talent/form"; 

    }
    
    // 수정 처리
    @PostMapping("/update")
    public String updateForm(@ModelAttribute TalentVO talentVO,
                                RedirectAttributes rttr){
        if (talentVO.getCategoryList() != null && !talentVO.getCategoryList().isEmpty()) {
            talentVO.setCategory(String.join(",", talentVO.getCategoryList()));
        }

        talentService.updateTalent(talentVO);

        rttr.addFlashAttribute("msg", "게시글이 성공적으로 수정되었습니다. ");
        return "redirect:/talent/detail?id=" + talentVO.getPostId();
    }

    // 삭제 처리
    @GetMapping("/delete")
    public String deleteTalent(@RequestParam("id") Long postId,
                                RedirectAttributes rttr,
                                Principal principal){
        if(principal == null){
            return "redirect:/auth/login";
        }

        TalentVO talent = talentService.getTalentById(postId);
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        Long loginMemberId = (long)loginUser.getMember_id();

        // 작성자 본인이 맞는지
        if (talent.getMemberId() != loginMemberId) {
            rttr.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/talent/detail?id=" + postId;
        }

        talentService.deleteTalent(postId);
        rttr.addFlashAttribute("msg", "게시글이 성공적으로 삭제되었습니다.");
        return "redirect:/talent/list";
    }


}