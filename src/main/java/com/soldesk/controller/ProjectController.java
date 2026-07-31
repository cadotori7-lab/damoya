package com.soldesk.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.service.CommentService;
import com.soldesk.service.MemberService;
import com.soldesk.service.ProjectService;
import com.soldesk.service.ReportService;
import com.soldesk.vo.CommentVO;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.PageBean;
import com.soldesk.vo.ProjectVO;
import com.soldesk.vo.ReportVO;



@Controller
@RequestMapping("/project")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ParticipationMapper participationMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReportService reportService;


    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long projectId, 
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model, Principal principal) {
        
        // 쿠키 이용해서 중복 방지
        Cookie[] cookies = request.getCookies();
        boolean isViewed = false;
        String viewCookieValue = ""; //기존 쿠키 값 저장용 

        if(cookies != null ){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("viewed_projects")){
                    viewCookieValue = cookie.getValue();
                    //해당 쿠키 안에 현재 프로젝트 id가 포함되어 있는지 검사
                    if(viewCookieValue.contains("[" + projectId + "]"))
                        isViewed = true;
                }
                break;
            }
        }

        //처음 조회하는 글이라면
        if(!isViewed){
            // DB에서 조회수 1 증가 
            projectService.increaseViewCount(projectId);

            // 쿠키 값 업데이트
            String newValue = viewCookieValue + "["+ projectId + "]";
            Cookie newCookie = new Cookie("viewed_projects", newValue);

            // 쿠키 설정
            newCookie.setMaxAge(60*60 *24); //24시간 유지
            newCookie.setPath("/");

            //응답에 쿠키 담아서 클라이언트로 전송
            response.addCookie(newCookie);
        }
                               
        ProjectVO project = projectService.getProjectById(projectId);
        List<CommentVO> commentList = commentService.getCommentsByProjectId(projectId);
        
        //프로젝트 작성자 정보 가져오기
        if (project.getOwnerId() != null){
            MemberVO owner = memberService.getMemberById(project.getOwnerId());
            model.addAttribute("owner", owner);
        }


        //  이미 지원했는지 체크 
        boolean hashApplied = false;
        
        // 이미 관심등록 했는지 체크
        boolean isLiked = false;
        
        // 내가 쓴 글인지 체크
        boolean isOwner = false; 

        // 로그인 상태인지 체크 
        if (principal != null) {
            String loginId = principal.getName();
            MemberVO loginUser = memberService.findByLoginId(loginId);
            model.addAttribute("member",loginUser);
            if (loginUser != null) {
                Long loginMemberId = (long) loginUser.getMember_id();
                
                // 지원 여부 확인
                int count = participationMapper.countByProjectAndMember(projectId, loginMemberId);
                hashApplied = (count > 0);

                //  현재 로그인한 유저 ID와 프로젝트의 ownerId가 같은지 비교
                if (project.getOwnerId() != null && project.getOwnerId().equals(loginMemberId)) {
                    isOwner = true;
                }

                //  유저가 좋아요를 눌렀는지 확인하는 로직 추가
                Map<String, Object> favParams = new HashMap<>();
                favParams.put("memberId", loginMemberId);
                favParams.put("projectId", projectId);
                int favCheck = projectService.checkFavorite(favParams); 
                isLiked = (favCheck > 0);
            }
        }

        model.addAttribute("hashApplied", hashApplied);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("isOwner", isOwner); 
        model.addAttribute("commentList", commentList);
        model.addAttribute("project", project);

        return "project/detail";
    }

    // 프로젝트 등록 폼 
    @GetMapping("/form")
    public String form() {
        return "project/form";
    }    

    // 프로젝트 등록
    @PostMapping("/register")
    public String registerProject(@ModelAttribute ProjectVO projectVO, 
                                    RedirectAttributes rttr,
                                    Principal principal) { 
        // 로그인 정보가 없는 경우 예외 처리 또는 로그인 페이지로 리다이렉트
        if (principal == null) {
            return "redirect:/auth/login";
        }

        // 현재 로그인한 유저의 정보 조회
        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);
        
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        //  로그인한 유저의 PK를 ownerId와 참여 리더로 세팅
        Long loginMemberId = (long) loginUser.getMember_id();
        projectVO.setOwnerId(loginMemberId); 

        //  프로젝트 등록 서비스 호출 (내부에 리더 자동 등록 로직 포함)
        projectService.registerProject(projectVO, loginMemberId);

        rttr.addFlashAttribute("msg", "프로젝트가 성공적으로 등록되었습니다.");
        return "redirect:/project/list";
    }
    
    //수정 페이지
    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long projectId,
                            Model model,
                            Principal principal, 
                            RedirectAttributes rttr){
        //  로그인 확인
        if (principal == null) {
            return "redirect:/auth/login";
        }                        

        ProjectVO project = projectService.getProjectById(projectId);
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        Long loginMemberId = (long) loginUser.getMember_id();

        // 작성자 본인이 맞는지 확인
        if(project.getOwnerId() == null || !project.getOwnerId().equals(loginMemberId)){
            rttr.addFlashAttribute("msg","수정 권한이 없습니다.");
            return "redirect:/project/detail?id=" + projectId;
        }

        model.addAttribute("project", project);
        model.addAttribute("mode", "update");//수정 모드로
        return "project/form";
    }
    
    //수정 처리
    @PostMapping("/update")
    public String updateForm(@ModelAttribute ProjectVO projectVO,
                                RedirectAttributes rttr)
    {
        projectService.updateProject(projectVO);

        rttr.addFlashAttribute("msg", "프로젝트가 성공적으로 수정되었습니다.");
        return "redirect:/project/detail?id=" + projectVO.getProjectId();
    }

    //삭제 처리 
    @GetMapping("/delete")
    public String deleteProject(@RequestParam("id") Long projectId,
                                    RedirectAttributes rttr, Principal principal) {
        if(principal == null){
            return "redirect:/auth/login";
        }

        ProjectVO project = projectService.getProjectById(projectId);
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        Long loginMemberId = (long)loginUser.getMember_id();

        //작성자 본인이 맞는지
        if(project.getOwnerId() == null || !project.getOwnerId().equals(loginMemberId)){
            rttr.addFlashAttribute("msg","삭제 권한이 없습니다.");
            return "redirect:/project/detail?id=" + projectId;
        }

        projectService.deleteProject(projectId);
        rttr.addFlashAttribute("msg", "프로젝트가 성공적으로 삭제되었습니다.");
        return "redirect:/project/list";
    }

    //지원하기
    @GetMapping("apply")
    public String applyForm(@RequestParam("id") Long projectId, Model model) {
       model.addAttribute("projectId", projectId);
       return "project/apply_form";
    }

    @GetMapping("/list")
    public String getProjectList(
            @RequestParam(value="page", defaultValue = "1") int page,
            @RequestParam(value="matchScope", defaultValue = "교내") String matchScope, 
            @RequestParam(value="tab", defaultValue = "all") String tab,
            @RequestParam(value="keyword", required = false) String keyword,
            @RequestParam(value="categoryList", required = false) String categoryList,
            @RequestParam(value="gradeList", required = false) String gradeList,     
            @RequestParam(value="sort", defaultValue = "latest") String sort,
            @RequestParam(value = "view", required = false) String view,
            Model model, Principal principal){

        // 페이지가 1 미만으로 내려가서 음수 오프셋이 발생하지 않도록 차단
        if (page < 1) {
            page = 1;
        }

        // 찜 목록 조회 모드일 때
        if ("favorite".equals(view)) {
            if (principal == null) {
                return "redirect:/auth/login";
            }
            MemberVO loginUser = memberService.findByLoginId(principal.getName());
            long memberId = (long) loginUser.getMember_id();
            
            List<ProjectVO> projectList = projectService.getFavoriteProjects(memberId);
            
            // 찜 목록일 때도 화면 아래에 0이 뜨거나 깨지지 않도록 pageBean 전달
            int totalFavoriteCnt = projectList.size();
            PageBean favoritePageBean = new PageBean(1, totalFavoriteCnt, 6); 
            
            model.addAttribute("projectList", projectList);
            model.addAttribute("pageBean", favoritePageBean);
            model.addAttribute("isFavoriteView", true); // 찜 모드 활성화 표시
            model.addAttribute("currentSort", sort); // 정렬 상태 유지
            
            return "project/list";
        }

        // 일반 목록(필터, 검색, 정렬) 조회 모드일 때
        ProjectVO vo = new ProjectVO();
        vo.setPage(page);
        vo.setMatchScope(matchScope);
        vo.setTab(tab);
        vo.setKeyword(keyword);
        vo.setSort(sort);
        vo.calcOffset();


        // 카테고리와 학년 파라미터를 List로 변환
        if(categoryList != null && !categoryList.trim().isEmpty()){
            vo.setCategoryList(Arrays.asList(categoryList.split(",")));
        }
        if (gradeList != null && !gradeList.trim().isEmpty()) {
            vo.setGradeList(Arrays.asList(gradeList.split(",")));
        }

        // DB에서 전체 프로젝트 가져오기
        int totalCnt = projectService.getTotalCount(vo);
        PageBean pageBean = new PageBean(page, totalCnt, 6);
        List<ProjectVO> projectList = projectService.getProjectList(vo);


        model.addAttribute("projectList", projectList);
        model.addAttribute("pageBean", pageBean);
        model.addAttribute("currentSort", sort);

        return "project/list";
    }
    
    // 관심 등록 및 취소
    @PostMapping("/favorite/toggle")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@RequestParam("projectId") long projectId, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        if(principal == null){
            response.put("status", "FAIL");
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);
        long memberId = (long) loginUser.getMember_id();

        boolean isLiked = projectService.toggleFavorite(memberId, projectId);
        int updateCount = projectService.getProjectById(projectId).getFavoriteCount();

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
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        long memberId = (long)loginUser.getMember_id();

        //목록 조회
        List<ProjectVO> projectList = projectService.getFavoriteProjects(memberId);

        model.addAttribute("projectList", projectList);
        model.addAttribute("isFavoriteView", true);

        return "project/list";
    }
    
    @GetMapping("/my")
    public String my() {
        return "project/my";
    }

    @PostMapping("/comment/add")
    public String addComment(@ModelAttribute CommentVO commentVO, RedirectAttributes rttr, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        if (loginUser == null) {
            return "redirect:/auth/login";
        }
        commentVO.setMember_id(loginUser.getMember_id());
        commentService.addComment(commentVO);
        rttr.addFlashAttribute("msg", "댓글이 성공적으로 등록되었습니다.");
        return "redirect:/project/detail?id=" + commentVO.getProject_id();
    }
    @PostMapping("/comment/update")
    public String updateComment(@ModelAttribute CommentVO commentVO, RedirectAttributes rttr, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        if (loginUser == null) {
            return "redirect:/auth/login";
        }
        commentVO.setMember_id(loginUser.getMember_id());
        commentService.updateComment(commentVO);
        rttr.addFlashAttribute("msg", "댓글이 수정되었습니다.");
        return "redirect:/project/detail?id=" + commentVO.getProject_id();
    }

    @PostMapping("/comment/delete")
    public String deleteComment(@RequestParam("comment_id") Long comment_id,
                                @RequestParam("projectId") Long projectId,
                                RedirectAttributes rttr,
                                Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        if (loginUser == null) {
            return "redirect:/auth/login";
        }
        commentService.deleteComment(comment_id);
        rttr.addFlashAttribute("msg", "댓글이 성공적으로 삭제되었습니다.");
        return "redirect:/project/detail?id=" + projectId;
    }

    @PostMapping("/report")
    public String reportProject(@RequestParam("targetId") Long targetId,
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

        // 신고 처리 로직 호출
        ReportVO reportVO = new ReportVO();
        reportVO.setTargetId(targetId);
        reportVO.setReporterId(loginUser.getMember_id());
        reportVO.setReason(reason);
        reportVO.setTargetType("PROJECT");

        reportService.addReport(reportVO);

        rttr.addFlashAttribute("msg", "프로젝트가 성공적으로 신고되었습니다.");
        return "redirect:/project/detail?id=" + targetId;
    }
        
}

