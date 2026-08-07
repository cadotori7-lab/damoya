package com.soldesk.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.mapper.MentorMapper;
import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.service.CommentService;
import com.soldesk.service.MemberService;
import com.soldesk.service.ParticipationService;
import com.soldesk.service.MentorService;
import com.soldesk.service.ProjectService;
import com.soldesk.service.ReportService;
import com.soldesk.service.UnivService;
import com.soldesk.vo.CommentVO;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.PageBean;
import com.soldesk.vo.ParticipationVO;
import com.soldesk.vo.ProjectVO;
import com.soldesk.vo.ReportVO;
import com.soldesk.vo.UnivVO;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@RequestMapping("/project")
public class ProjectController {

    private final Logger logger = Logger.getLogger(ProjectController.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ProjectService projectService;

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ParticipationMapper participationMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private MentorMapper mentorMapper;

    @Autowired
    private UnivService univService;

    @Autowired
    private MentorService mentorService;


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
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberVO member = memberService.findByLoginId(member_id);
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

        // 멘토인지 체크
        boolean isMentor = false;

        // 로그인 상태인지 체크 
        if (principal != null) {
            String loginId = principal.getName();
            MemberVO loginUser = memberService.findByLoginId(loginId);
            model.addAttribute("member",loginUser);
            
            if (loginUser != null) {
                Long loginMemberId = (long) loginUser.getMember_id();
                
                // 멘토 여부 확인
                isMentor = mentorService.isMentor(loginMemberId.intValue());                // 지원 여부 확인
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
        model.addAttribute("isMentor", isMentor);
        model.addAttribute("commentList", commentList);
        model.addAttribute("project", project);
        model.addAttribute("member", member);

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
        
        //  로그인 정보가 없는 경우 로그인 페이지로 리다이렉트
        if (principal == null) {
            rttr.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
            return "redirect:/auth/login";
        }

        //  로그인 유저 정보 조회
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        if (loginUser == null) {
            rttr.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        }

        //  멘토 여부 확인 (멘토는 프로젝트 게시글 작성 불가)
        boolean isMentor = mentorService.isMentor(loginUser.getMember_id());
        if (isMentor) {
            rttr.addFlashAttribute("msg", "멘토는 프로젝트 게시글을 작성할 수 없습니다.");
            return "redirect:/project/list";
        }

        //  교내 매칭 등록 시 학교 인증(관리자 승인) 여부 체크
        if ("교내".equals(projectVO.getMatchScope()) && !loginUser.isApproved()) {
            rttr.addFlashAttribute("msg", "교내 매칭 프로젝트는 학교 인증 완료 후 등록 가능합니다.");
            return "redirect:/project/list";
        }

        //  로그인한 유저의 PK를 ownerId 및 리더로 세팅
        Long loginMemberId = (long) loginUser.getMember_id();
        projectVO.setOwnerId(loginMemberId); 

        //  프로젝트 등록 서비스 호출 
        projectService.registerProject(projectVO, loginMemberId);

        rttr.addFlashAttribute("msg", "프로젝트가 성공적으로 등록되었습니다.");
        return "redirect:/project/list";
    }

    // 수정 페이지
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
        model.addAttribute("mode", "update");
        return "project/form";
    }

    // 수정 처리
    @PostMapping("/update")
    public String updateForm(@ModelAttribute ProjectVO projectVO,
                                RedirectAttributes rttr)
    {
        projectService.updateProject(projectVO);

        rttr.addFlashAttribute("msg", "프로젝트가 성공적으로 수정되었습니다.");
        return "redirect:/project/detail?id=" + projectVO.getProjectId();
    }

    // 삭제 처리
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
    // 지원하기 폼
    @GetMapping("/apply")
    public String applyForm(@RequestParam("id") Long projectId, Model model) {
       model.addAttribute("projectId", projectId);
       return "project/apply_form";
    }

    //지원하기
    @PostMapping("/apply")
    public String applyProject(@RequestParam("id") Long projectId, 
                           Principal principal,
                            RedirectAttributes rttr) {
        if(principal == null){
            rttr.addFlashAttribute("msg","로그인 후 이용 가능합니다.");
            return "redirect:/auth/login";
        }

        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        boolean isMentor = mentorService.isMentor(loginUser.getMember_id());

        // 멘토인 경우 지원 안됨
        if(isMentor){
            rttr.addFlashAttribute("msg", "멘토 계정은 프로젝트에 지원할 수 없습니다.");
            return "redirect:/project/detail?id=" + projectId;
        }
        rttr.addFlashAttribute("msg", "프로젝트 지원이 완료되었습니다.");
        return "redirect:/project/detail?id=" + projectId;
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

        boolean isLoginRequired = false;    // 로그인 했는지
        boolean isApprovalRequired = false; // 승인 대기 상태인지 
        boolean isMentor = false;           // 멘토인지 여부
        boolean isExternalMentor = false;   // 외부 멘토인지 여부
        
        ProjectVO vo = new ProjectVO(); 
        
        // 카테고리와 학년 파라미터 셋팅
        if(categoryList != null && !categoryList.trim().isEmpty()){
            vo.setCategoryList(Arrays.asList(categoryList.split(",")));
        }
        if (gradeList != null && !gradeList.trim().isEmpty()) {
            vo.setGradeList(Arrays.asList(gradeList.split(",")));
        }

        // 로그인 여부에 따른 처리
        if(principal != null){
            MemberVO loginUser = memberService.findByLoginId(principal.getName());
            long memberId = loginUser.getMember_id();

            //  멘토 여부 확인
            isMentor = mentorService.isMentor(Long.valueOf(memberId).intValue());            

            //  대학/학과 소속 여부 및 학교 인증 여부 확인
            boolean hasDept = (loginUser.getDept_id() != null && loginUser.getDept_id() > 0);
            boolean isApproved = loginUser.isApproved();

            //  외부 멘토 
            isExternalMentor = isMentor && !hasDept;

            // 외부 멘토는 전국만 볼 수 있
            if(isExternalMentor){
                matchScope = "전국";
            }

            // 아직 승인x 학생 및 내부 멘토
            if ("교내".equals(matchScope) && hasDept && !isApproved) {
                isApprovalRequired = true;
            }

            if(hasDept){
                UnivVO univ = univService.getUnivByDeptId(loginUser.getDept_id().intValue());
                if(univ != null){
                    vo.setUserUnivName(univ.getUniv_name());
                }
                vo.setUserDeptId(loginUser.getDept_id().longValue());
            }
            
            model.addAttribute("isMentor", isMentor);
            model.addAttribute("isExternalMentor", isExternalMentor);
            model.addAttribute("isApprovalRequired", isApprovalRequired);
            
        } else {
            // 비로그인 상태 처리
            boolean hasDeptCategory = vo.getCategoryList() != null && vo.getCategoryList().contains("학과");
            if("교내".equals(matchScope) || hasDeptCategory){
                isLoginRequired = true;
            }
        }

        // 페이지 음수 방지
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
            
            int totalFavoriteCnt = projectList.size();
            PageBean favoritePageBean = new PageBean(1, totalFavoriteCnt, 6); 
            
            model.addAttribute("projectList", projectList);
            model.addAttribute("pageBean", favoritePageBean);
            model.addAttribute("isFavoriteView", true); 
            model.addAttribute("currentSort", sort); 
            

            return "project/list";
        }

        vo.setPage(page);
        vo.setMatchScope(matchScope); 
        vo.setTab(tab);
        vo.setKeyword(keyword);
        vo.setSort(sort);
        vo.calcOffset();

        List<ProjectVO> projectList = new ArrayList<>();
        int totalCnt = 0;
        
        // 로그인이 필요한 조건(교내/학과)인데 비로그인 상태라면 DB 조회를 건너뜀
        if (!isLoginRequired && !isApprovalRequired) {
            totalCnt = projectService.getTotalCount(vo);
            projectList = projectService.getProjectList(vo);
        }
        
        PageBean pageBean = new PageBean(page, totalCnt, 6);

        model.addAttribute("projectList", projectList);
        model.addAttribute("pageBean", pageBean);
        model.addAttribute("currentSort", sort);
        model.addAttribute("isLoginRequired", isLoginRequired); 
        model.addAttribute("matchScope", matchScope); // 강제 변경된 매칭 범위 뷰로 전달

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
    

    // 멘토 추천 화면 (즉시 표시 → JS가 API로 추천 요청)
    @GetMapping("/mentor-recommend")
    public String mentorRecommend(@RequestParam("id") Long projectId, Model model) {
        ProjectVO project = projectService.getProjectById(projectId);
        if (project == null) {
            return "redirect:/project/list";
        }

        String projectName = safe(project.getTitle());
        String projectDescription = buildMentorMatchDescription(project);

        model.addAttribute("project", project);
        model.addAttribute("projectName", projectName);
        model.addAttribute("projectDescription", projectDescription);
        model.addAttribute("autoMatch", projectDescription.length() >= 10);

        if (projectDescription.length() < 10) {
            model.addAttribute(
                "error",
                "멘토 추천을 위해 프로젝트 태그 또는 소개글이 더 필요해요. (합쳐서 10자 이상)"
            );
        }

        return "project/mentor-recommend";
    }

    // 멘토 추천 API: DB 태그+소개를 합쳐 FastAPI에 요청
    @GetMapping("/mentor-recommend/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> mentorRecommendApi(
            @RequestParam("id") Long projectId) {
        ProjectVO project = projectService.getProjectById(projectId);
        if (project == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok", false,
                "error", "프로젝트를 찾을 수 없습니다."
            ));
        }

        String projectName = safe(project.getTitle());
        String projectDescription = buildMentorMatchDescription(project);
        String reference = "project-" + project.getProjectId() + "-" + UUID.randomUUID();

        if (projectDescription.length() < 10) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok", false,
                "error", "멘토 추천을 위해 프로젝트 태그 또는 소개글이 더 필요해요. (합쳐서 10자 이상)"
            ));
        }

        logger.info(
            "프로젝트 멘토 추천 요청 projectId="
                + projectId
                + ", projectName="
                + projectName
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("reference", reference);
            body.put("projectName", projectName);
            body.put("projectDescription", projectDescription);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = restTemplate.postForObject(
                fastApiBaseUrl + "/mentor-match",
                request,
                Map.class
            );

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("projectName", projectName);
            response.put("projectDescription", projectDescription);
            response.put("reference", reference);
            response.put("matchResult", result == null ? Map.of() : result);
            return ResponseEntity.ok(response);
        } catch (HttpStatusCodeException e) {
            logger.warning("FastAPI 멘토 추천 오류: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                "ok", false,
                "error", "멘토 추천 요청이 실패했습니다: " + e.getResponseBodyAsString()
            ));
        } catch (ResourceAccessException e) {
            logger.warning("FastAPI 멘토 추천 연결 실패: " + e.getMessage());
            return ResponseEntity.status(503).body(Map.of(
                "ok", false,
                "error", "Python 서버에 연결할 수 없습니다. localhost:8501 서버를 확인하세요."
            ));
        }
    }

    // AI 추천 멘토에게 참여 제안 (프로젝트 팀장만 가능)
    @PostMapping("/mentor-recommend/offer")
    @ResponseBody
    public ResponseEntity<?> offerMentor(@RequestParam("projectId") Long projectId,
                                          @RequestParam("mentorMemberId") Long mentorMemberId,
                                          Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("ok", false, "error", "로그인이 필요합니다."));
        }
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("ok", false, "error", "회원 정보를 찾을 수 없습니다."));
        }

        try {
            participationService.offerMentor(projectId, loginUser.getMember_id(), mentorMemberId);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("ok", false, "error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "error", e.getMessage()));
        }
    }

    //태그 + 소개글을 한 줄로 합쳐 FastAPI projectDescription 으로 보내기
    private String buildMentorMatchDescription(ProjectVO project) {
        String tags = safe(project.getTags());
        String summary = safe(project.getSummary());
        return Stream.of(tags, summary)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining(" "))
            .trim();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    @GetMapping("/my")
    public String my(Model model, Principal principal) {
        List<ParticipationVO> project = participationMapper.selectParticipationListByMemberId((int) memberService.findByLoginId(principal.getName()).getMember_id(), 0);
        model.addAttribute("project", project);
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

    // 댓글 신고 (댓글 옆 신고 버튼에서 fetch()로 호출)
    @PostMapping("/comment/report")
    @ResponseBody
    public ResponseEntity<?> reportComment(@RequestParam("comment_id") int commentId,
                                            @RequestParam("reason") String reason,
                                            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        MemberVO loginUser = memberService.findByLoginId(principal.getName());
        if (loginUser == null) {
            return ResponseEntity.status(401).build();
        }

        ReportVO reportVO = new ReportVO();
        reportVO.setTargetId((long) commentId);
        reportVO.setReporterId(loginUser.getMember_id());
        reportVO.setReason(reason);
        reportVO.setTargetType("COMMENT");

        reportService.addReport(reportVO);

        return ResponseEntity.ok().build();
    }
}

