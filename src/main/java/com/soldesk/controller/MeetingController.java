package com.soldesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk.service.MeetingService;
import com.soldesk.service.MemberService;
import com.soldesk.vo.MeetingVO;
import com.soldesk.vo.MemberVO;



@Controller
@RequestMapping("/workspace/{project_id}/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MemberService memberService;
    
    @GetMapping({"", "/{meeting_id}"})
    public String meetings(@PathVariable("project_id") long project_id,
                            @PathVariable(value = "meeting_id", required = false) Long meeting_id,
                            Model model) {
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberVO member = memberService.findByLoginId(member_id);
        MeetingVO meeting = meeting_id != null ? meetingService.selectMeetingById(meeting_id) : null;
        model.addAttribute("meeting", meeting);
        model.addAttribute("meetingList", meetingService.selectMeetingsByProjectId(project_id));
        model.addAttribute("project_id", project_id);
        model.addAttribute("myRole", "LEADER");
        model.addAttribute("member", member);
        return "workspace/meetings";
    }
    @GetMapping({"/form", "/{meeting_id}/edit"})
    public String meetingForm(@PathVariable("project_id") long project_id,
                             Model model,
                             @PathVariable(value = "meeting_id", required = false) Long meeting_id) {
        model.addAttribute("project_id", project_id);
        model.addAttribute("meeting", meeting_id != null ? meetingService.selectMeetingById(meeting_id) : new MeetingVO());
        return "workspace/meeting-form";
    }
    @PostMapping({"/form/insert", "/{meeting_id}/edit"})
    public String newMeeting(MeetingVO meeting, 
                            @PathVariable("project_id") long project_id,
                            @PathVariable(value = "meeting_id", required = false) Long meeting_id,
                            Model model) {
        if (meeting_id == null) {
            meetingService.insertMeeting(meeting);
        } else {
            meeting.setMeeting_id(meeting_id);
            meetingService.updateMeeting(meeting);
        }
        return "redirect:/workspace/" + project_id + "/meetings";
    }
    @PostMapping("/{meeting_id}/delete")
    public String deleteMeeting(@PathVariable("project_id") long project_id,
                                @PathVariable("meeting_id") long meeting_id) {
        meetingService.deleteMeeting(meeting_id);
        return "redirect:/workspace/" + project_id + "/meetings";
    }
}
