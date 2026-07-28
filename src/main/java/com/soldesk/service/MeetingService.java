package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soldesk.mapper.MeetingMapper;
import com.soldesk.vo.MeetingVO;

@Service
public class MeetingService {

    @Autowired
    private MeetingMapper meetingMapper;


    public void insertMeeting(MeetingVO meeting) {
        meetingMapper.insertMeeting(meeting);
    }
    public void updateMeeting(MeetingVO meeting) {
        meetingMapper.updateMeeting(meeting);
    }
    public void deleteMeeting(Long meeting_id) {
        meetingMapper.deleteMeeting(meeting_id);
    }
    public MeetingVO selectMeetingById(Long meeting_id) {
        return meetingMapper.selectMeetingById(meeting_id);
    }
    public List<MeetingVO> selectMeetingsByProjectId(Long project_id) {
        return meetingMapper.selectMeetingsByProjectId(project_id);
    }
    
}
