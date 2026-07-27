package com.soldesk.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.service.CommentService;
import com.soldesk.service.MemberService;
import com.soldesk.service.ProjectService;
import com.soldesk.vo.CommentVO;
import com.soldesk.vo.MemberVO;
import com.soldesk.vo.ProjectVO;



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


    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 프로젝트 목록 페이지
    @GetMapping("/list")
    public String list(Model model) {
        
        List<ProjectVO> projectList = projectService.getAllProjects();

        model.addAttribute("projectList", projectList);

        return "project/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long projectId, Model model, Principal principal) {
        ProjectVO project = projectService.getProjectById(projectId);

        List<CommentVO> commentList = commentService.getCommentsByProjectId(projectId);
        
        // 1. 이미 지원했는지 체크 (기존 코드)
        boolean hashApplied = false;
        
        // 2. 내가 쓴 글(작성자)인지 체크하는 변수 추가
        boolean isOwner = false; 

        if (principal != null) {
            String loginId = principal.getName();
            MemberVO loginUser = memberService.findByLoginId(loginId);
            model.addAttribute("member",loginUser);
            if (loginUser != null) {
                Long loginMemberId = (long) loginUser.getMember_id();
                
                // 지원 여부 확인
                int count = participationMapper.countByProjectAndMember(projectId, loginMemberId);
                hashApplied = (count > 0);

                // ★ 현재 로그인한 유저 ID와 프로젝트의 ownerId가 같은지 비교
                if (project.getOwnerId() != null && project.getOwnerId().equals(loginMemberId)) {
                    isOwner = true;
                }
            }
        }
        
        model.addAttribute("hashApplied", hashApplied);
        model.addAttribute("isOwner", isOwner); // ★ 뷰로 전달
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
        // 1. 로그인 정보가 없는 경우 예외 처리 또는 로그인 페이지로 리다이렉트
        if (principal == null) {
            return "redirect:/auth/login";
        }

        // 2. 현재 로그인한 유저의 정보 조회
        String loginId = principal.getName();
        MemberVO loginUser = memberService.findByLoginId(loginId);
        
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        // 3. 로그인한 유저의 PK(member_id)를 ownerId와 참여 리더로 세팅
        Long loginMemberId = (long) loginUser.getMember_id();
        projectVO.setOwnerId(loginMemberId); // ★ 이 부분이 빠져있어서 null이 들어갔던 것입니다!

        // 4. 프로젝트 등록 서비스 호출 (내부에 리더 자동 등록 로직 포함)
        projectService.registerProject(projectVO, loginMemberId);

        rttr.addFlashAttribute("msg", "프로젝트가 성공적으로 등록되었습니다.");
        return "redirect:/project/list";
    }
    
    //수정 페이지
    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long projectId,
                            Model model){
        ProjectVO project = projectService.getProjectById(projectId);
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
                                    RedirectAttributes rttr) {
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
    @PostMapping("/comment/delete")
    public String deleteComment(@RequestParam("commentId") Long commentId,
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
        commentService.deleteComment(commentId);
        rttr.addFlashAttribute("msg", "댓글이 성공적으로 삭제되었습니다.");
        return "redirect:/project/detail?id=" + projectId;
    }
}
