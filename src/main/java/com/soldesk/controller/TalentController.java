package com.soldesk.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.mapper.MentorMapper;
import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.service.CommentService;
import com.soldesk.service.MemberService;
import com.soldesk.service.ParticipationService;
import com.soldesk.service.ReportService;
import com.soldesk.service.TalentService;
import com.soldesk.vo.CommentVO;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.PageBean;
import com.soldesk.vo.ParticipationVO;
import com.soldesk.vo.ProjectVO;
import com.soldesk.vo.ReportVO;
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
    private MentorMapper mentorMapper;

    @Autowired
    private ReportService reportService;


    TalentController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long postId, 
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model, Principal principal){
                            
        // 쿠키 이용해서 중복 방지
        Cookie[] cookies = request.getCookies();
        boolean isViewed = false;
        String viewCookieValue = ""; //기존 쿠키 값 저장용

        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("viewd_talents")){
                    viewCookieValue = cookie.getValue();
                    //해당 쿠키 안에 현재 인재글id가 포함되어 있는지 검사
                    if(viewCookieValue.contains("[" + postId + "]")){
                        isViewed = true;
                    }
                    break;
                }
            }
        }
        //처음 조회하는 글
        if(!isViewed){
            //조회수 증가
            talentService.increaseViewCount(postId);

            //쿠키에 현재 인재글id 추가
            String newValue = viewCookieValue + "[" + postId + "]";
            Cookie newCookie = new Cookie("viewd_talents", newValue);
            
            // 쿠키 설정
            newCookie.setPath("/"); // 모든 경로에서 접근 가능하도록 설정
            newCookie.setMaxAge(60 * 60 * 24); // 쿠키 유효기간: 1일
            
            //응답에 쿠키 담아서 클라이언트로 전송 
            response.addCookie(newCookie);
        }
        
        // 해당 게시글 데이터 가져오기
        TalentVO talent = talentService.getTalentById(postId);
        // 해당 게시글의 댓글 가져오기
        List<CommentVO> comments = commentService.getCommentsByPostId(postId);

        // 작성자 본인이 맞는지 확인
        boolean isOwner = false;
        MemberVO loginUser = null;
        List<ProjectVO> leaderProjects = null;
        List<Long> offeredProjectIds = null;    // 이미 제의한 프로젝트 ID 목록


        //이미 관심등록 했는지 체크
        boolean isLiked = false;

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
                // 이미 제의한 프로젝트 ID 목록 가져오기
                offeredProjectIds = talentService.getAlreadyOfferedProjectIds(talent.getMemberId());
      
                // 유저가 좋아요 눌렀는지 확인하는 로직을 반드시 로그인 유저가 있을 때만 실행하도록 내부로 이동!
                Map<String,Object> favParams = new HashMap<>();
                favParams.put("memberId", (long) loginUser.getMember_id());
                favParams.put("postId", postId);
                int favCheck = talentService.checkFavorite(favParams);
                isLiked = (favCheck > 0);
            }
        }


        model.addAttribute("leaderProjects", leaderProjects);
        model.addAttribute("offeredProjectIds", offeredProjectIds);
        model.addAttribute("talent", talent);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("member", loginUser);
        model.addAttribute("commentList", comments);
        model.addAttribute("isLiked", isLiked); 


        return "talent/detail";
    }

   // 함께하기 제의
    @PostMapping("/offer/send")
    public String sendOffer(ParticipationVO participationVO,
                            @RequestParam("postId") Long postId,
                            @RequestParam("projectIds") Long[] projectIds,
                            Principal principal,
                            RedirectAttributes rttr) {

        if (projectIds == null || projectIds.length == 0) {
        rttr.addFlashAttribute("msg", "초대할 프로젝트를 하나 이상 선택해주세요.");
        return "redirect:/talent/detail?id=" + postId;
        }

        // 로그인 체크
        if(principal == null){
            System.out.println(">>> 에러: 로그인 정보가 없습니다.");
            rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
            return "redirect:/auth/login";
        }

        // 회원 정보 조회
        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);

        if(loginUser == null){
            System.out.println(">>> 에러: 로그인 유저 정보를 찾을 수 없습니다.");
            rttr.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        }

        // Offer로 고정 역할은 Member로
        participationVO.setJoinStatus("OFFER");
        participationVO.setProjectRole("MEMBER");
        
        int successCount = 0;
        
        try {
            for (Long projectId : projectIds) {
            participationVO.setProjectId(projectId);
                try{
                    talentService.insertOffer(participationVO);
                    successCount++;
                } catch (org.springframework.dao.DuplicateKeyException e) {
                    System.out.println(">>> 중복 제의: 이미 해당 프로젝트에 제의를 보냈거나 참여 중인 인재입니다! (Project ID: " + projectId + ")");
                }
            }
        if(successCount > 0){
            rttr.addFlashAttribute("msg", successCount + "개의 프로젝트에 제의가 성공적으로 전송되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "모든 프로젝트에 대한 제의가 실패했습니다. 이미 제의를 보냈거나 참여 중인 인재일 수 있습니다.");
        }
        } catch (Exception e) {
            System.out.println(">>> 에러: 제의 전송 중 문제가 발생했습니다. " + e.getMessage());
            rttr.addFlashAttribute("msg", "제의 전송 중 문제가 발생했습니다. 다시 시도해주세요.");
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
                               RedirectAttributes rttr,
                               Principal principal){
        
        if(principal == null){
            rttr.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
            return "redirect:/auth/login";
        }
        
        // 현재 로그인한 유저의 정보 조회
        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);

        if(loginUser == null){
            rttr.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        } 

        int mentorCheck = mentorMapper.selectMentorById((long) loginUser.getMember_id());
        boolean isMentor = (mentorCheck >0);
        
       if(isMentor) {
            talentVO.setKind("MENTOR");
       }
        // 다중 체크된 카테고리 리스트를 콤마(,)로 연결하여 문자열로 변환
        if (talentVO.getCategoryList() != null && !talentVO.getCategoryList().isEmpty()) {
            talentVO.setCategory(String.join(",", talentVO.getCategoryList()));
        }

        Long loginMemberId = (long) loginUser.getMember_id();

        talentService.registerTalent(talentVO, loginMemberId);

        rttr.addFlashAttribute("msg", "게시글이 성공적으로 등록되었습니다!");
        return "redirect:/talent/list";
    }

    @GetMapping("/list")
    public String getTalentList(
            @RequestParam(value="page", defaultValue = "1") int page,
            @RequestParam(value="matchScope", defaultValue = "교내") String matchScope, 
            @RequestParam(value="kind", defaultValue = "all") String kind,            
            @RequestParam(value="keyword", required = false) String keyword,
            @RequestParam(value="categoryList", required = false) String categoryList,
            @RequestParam(value="sort", defaultValue = "latest") String sort,
            @RequestParam(value = "view", required = false) String view,
            Model model, Principal principal) {

            if(page < 1){
                page = 1;
            }

            //찜 목록 조회 모드일 때 
            if("favorite".equals(view)){
                if(principal == null){
                    return "redirect:/auth/login";
                }
                MemberVO loginUser = memberService.findByLoginId(principal.getName());
                long memberId = (long) loginUser.getMember_id();

                TalentVO vo = new TalentVO();
                vo.setPage(page);
                vo.setMemberId(memberId);
                vo.setMatchScope(matchScope);
                vo.setKind(kind);
                vo.setKeyword(keyword);
                vo.setSort(sort);
                
                if(categoryList != null){
                    vo.setCategoryList(Arrays.asList(categoryList.split(",")));
                }

                // 서비스와 매퍼가 필터가 적용된 찜 목록을 조회할 수 있도록 vo를 전달합니다.
                // (만약 기존 getFavoriteTalents가 파라미터로 long만 받았다면 서비스/매퍼 메서드도 vo를 받도록 수정이 필요할 수 있습니다)
                List<TalentVO> talentList = talentService.getFavoriteTalents(vo);

                int totalFavoriteCnt = talentList.size();
                PageBean favoritePageBean = new PageBean(page, totalFavoriteCnt, 6);

                model.addAttribute("talentList", talentList);
                model.addAttribute("pageBean", favoritePageBean);
                model.addAttribute("isFavoriteView", true);
                model.addAttribute("currentSort", sort);
                model.addAttribute("matchScope", matchScope); 
                model.addAttribute("kind", kind);

                return "talent/list";
            }

            //  일반 목록 조회 모드일 때
            TalentVO vo = new TalentVO();
            vo.setPage(page);
            vo.setMatchScope(matchScope);
            vo.setKind(kind);
            vo.setKeyword(keyword);
            vo.setSort(sort);
            
            // 카테고리를 list로 변환
            if(categoryList != null){
                vo.setCategoryList(Arrays.asList(categoryList.split(",")));
            }

            // DB에서 전체 프로젝트 가져오기
            int totalCnt = talentService.getTotalCount(vo);
            PageBean pageBean = new PageBean(page, totalCnt, 6);
            List<TalentVO> talentList = talentService.getTalentList(vo);
            
            model.addAttribute("talentList", talentList);
            model.addAttribute("pageBean", pageBean);
            model.addAttribute("currentSort", sort);

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
        rttr.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
        return "redirect:/auth/login";
    }

    TalentVO talent = talentService.getTalentById(postId);
    if(talent == null){
        rttr.addFlashAttribute("msg","존재하지 않는 게시글입니다.");
        return "redirect:/talent/list";
    }

    MemberVO loginUser = memberService.findByLoginId(principal.getName());
    long loginMemberId = loginUser.getMember_id();

    // 작성자 본인이 맞는지 확인
    if (talent.getMemberId() != loginMemberId) {
        rttr.addFlashAttribute("msg", "권한이 없습니다.");
        return "redirect:/talent/detail?id=" + postId;
    }
    
    // 멘토 여부 확인
    int mentorCheck = mentorMapper.selectMentorById(loginMemberId);
    boolean isMentor = (mentorCheck > 0);

    model.addAttribute("talent", talent);
    model.addAttribute("mode", "update");
    model.addAttribute("isMentor", isMentor);
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

    // 관심 등록
    @PostMapping("/favorite/toggle")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@RequestParam("postId") long postId,
                                                Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        if(principal == null){
            response.put("status", "FAIL");
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);
        long memberId = (long) loginUser.getMember_id();

        boolean isLiked = talentService.toggleFavorite(memberId, postId);
        int updateCount = talentService.getTalentById(postId).getFavoriteCount();

        response.put("status", "SUCCESS");
        response.put("isLiked", isLiked);
        response.put("favoriteCount", updateCount);

        return response;

    }
    //관심 등록한 프로젝트 페이지
    @GetMapping("/favorites")
    public String myFavorites(Model model, Principal principal){
        //로그인 안한 경우 로그인 페이지로
        if(principal == null){
            return "redirect:/auth/login";
        }

        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);
        if(loginUser == null){
            return "redirect:/auth/login";
        }

        long memberId = (long)loginUser.getMember_id();

        //목록 조회
        List<TalentVO> talentlist = talentService.getFavoriteTalents(memberId);

        model.addAttribute("talentList", talentlist);
        model.addAttribute("isFavoriteView", true);

        return "talent/list";
    }



    // 댓글추가
    @PostMapping("/comment/add")
    public String addComment(@ModelAttribute CommentVO commentVO,
                             RedirectAttributes rttr,
                             Principal principal) {
        if (principal == null) {
            rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
            return "redirect:/auth/login";
        }

        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);

        if (loginUser == null) {
            rttr.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        }
        commentVO.setMember_id(loginUser.getMember_id());
        commentService.addTalentComment(commentVO);
        rttr.addFlashAttribute("msg", "댓글이 성공적으로 등록되었습니다.");
        return "redirect:/talent/detail?id=" + commentVO.getPost_id();
    }
    @PostMapping("/comment/delete")
    public String deleteComment(@RequestParam("commentId") Long commentId,
                                @RequestParam("postId") Long postId,
                                RedirectAttributes rttr,
                                Principal principal) {
        if (principal == null) {
            rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
            return "redirect:/auth/login";
        }

        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);

        if (loginUser == null) {
            rttr.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        }

        // 댓글 삭제
        commentService.deleteComment(commentId);
        rttr.addFlashAttribute("msg", "댓글이 성공적으로 삭제되었습니다.");
        return "redirect:/talent/detail?id=" + postId;
    }

    @PostMapping("/comment/update")
    public String updateComment(@ModelAttribute CommentVO commentVO,
                                RedirectAttributes rttr,
                                Principal principal) {
        if (principal == null) {
            rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
            return "redirect:/auth/login";
        }

        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);

        if (loginUser == null) {
            rttr.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        }

        // 댓글 수정 (본인 댓글만 수정되도록 로그인 유저 id로 채움)
        commentVO.setMember_id(loginUser.getMember_id());
        commentService.updateComment(commentVO);
        rttr.addFlashAttribute("msg", "댓글이 성공적으로 수정되었습니다.");
        return "redirect:/talent/detail?id=" + commentVO.getPost_id();
    }

    // 인재풀 게시글 신고
    @PostMapping("/report")
    public String reportTalent(@RequestParam("targetId") Long targetId,
                                @RequestParam("reason") String reason,
                                RedirectAttributes rttr,
                                Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        ReportVO reportVO = new ReportVO();
        reportVO.setTargetId(targetId);
        reportVO.setReporterId(loginUser.getMember_id());
        reportVO.setReason(reason);
        reportVO.setTargetType("POST");

        reportService.addReport(reportVO);

        rttr.addFlashAttribute("msg", "게시글이 성공적으로 신고되었습니다.");
        return "redirect:/talent/detail?id=" + targetId;
    }

}