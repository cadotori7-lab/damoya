package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.ParticipationMapper;
import com.soldesk.vo.ParticipationVO;

@Service
public class ParticipationService {

    @Autowired
    private ParticipationMapper participationMapper;

    // 업무 담당자로 선택 가능한 참여자 목록
    @Transactional(readOnly = true)
    public List<ParticipationVO> selectTaskMembers(long project_id) {
        return participationMapper.selectTaskMembers(project_id);
    }

    // 해당 사용자가 프로젝트 팀장인지 확인
    @Transactional(readOnly = true)
    public boolean isLeader(long project_id, long member_id) {
        return participationMapper.countLeader(
                project_id,
                member_id
        ) > 0;
    }

    // 해당 사용자가 업무 담당자로 선택 가능한지 확인
    @Transactional(readOnly = true)
    public boolean isTaskMember(long project_id, long member_id) {
        return participationMapper.countTaskMember(
                project_id,
                member_id
        ) > 0;
    }
}