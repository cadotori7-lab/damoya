package com.soldesk.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.MeetingService;
import com.soldesk.service.MemberService;
import com.soldesk.service.ParticipationService;
import com.soldesk.vo.MeetingVO;
import com.soldesk.vo.MemberVO;



@Controller
@RequestMapping("/workspace/{project_id}/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ParticipationService participationService;
    
    @GetMapping({"", "/{meeting_id}"})
    public String meetings(@PathVariable("project_id") long project_id,
                            @PathVariable(value = "meeting_id", required = false) Long meeting_id,
                            Principal principal,
                            Model model) {
        MemberVO member = getLoginMember(principal);
        MeetingVO meeting = meeting_id != null ? meetingService.selectMeetingById(meeting_id) : null;
        model.addAttribute("meeting", meeting);
        model.addAttribute("meetingList", meetingService.selectMeetingsByProjectId(project_id));
        model.addAttribute("project_id", project_id);
        model.addAttribute("myRole", member == null ? null
                : participationService.getProjectRole(
                        project_id, member.getMember_id()));
        model.addAttribute("member", member);
        return "workspace/meetings";
    }
    @GetMapping({"/form", "/{meeting_id}/edit"})
    public String meetingForm(@PathVariable("project_id") long project_id,
                             Principal principal,
                             Model model,
                             @PathVariable(value = "meeting_id", required = false) Long meeting_id) {
        requireLeader(project_id, principal);
        model.addAttribute("project_id", project_id);
        model.addAttribute("meeting", meeting_id != null ? meetingService.selectMeetingById(meeting_id) : new MeetingVO());
        return "workspace/meeting-form";
    }
    @PostMapping({"/form/insert", "/{meeting_id}/edit"})
    public String newMeeting(MeetingVO meeting, 
                            @PathVariable("project_id") long project_id,
                            @PathVariable(value = "meeting_id", required = false) Long meeting_id,
                            Principal principal,
                            Model model) {
        requireLeader(project_id, principal);
        if (meeting_id == null) {
            meeting.setProject_id(project_id);
            meetingService.insertMeeting(meeting);
        } else {
            meeting.setMeeting_id(meeting_id);
            meetingService.updateMeeting(meeting);
        }
        return "redirect:/workspace/" + project_id + "/meetings";
    }
    @PostMapping("/{meeting_id}/delete")
    public String deleteMeeting(@PathVariable("project_id") long project_id,
                                @PathVariable("meeting_id") long meeting_id,
                                Principal principal) {
        requireLeader(project_id, principal);
        meetingService.deleteMeeting(meeting_id);
        return "redirect:/workspace/" + project_id + "/meetings";
    }

    private MemberVO getLoginMember(Principal principal) {
        return principal == null
                ? null
                : memberService.findByLoginId(principal.getName());
    }

    private void requireLeader(long projectId, Principal principal) {
        MemberVO member = getLoginMember(principal);
        if (member == null || !participationService.isLeader(
                projectId, member.getMember_id())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "프로젝트 팀장만 회의를 변경할 수 있습니다.");
        }
    }
}
