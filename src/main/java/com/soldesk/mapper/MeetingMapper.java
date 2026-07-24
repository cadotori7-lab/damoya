package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.MeetingVO;

public interface MeetingMapper {

    void insertMeeting(MeetingVO meeting); // 회의록 등록
    void updateMeeting(MeetingVO meeting); // 회의록 수정
    void deleteMeeting(Long meeting_id); // 회의록 삭제
    MeetingVO selectMeetingById(Long meeting_id); // 회의록 조회
    List<MeetingVO> selectMeetingsByProjectId(Long project_id); // 프로젝트별 회의록 조회
}
