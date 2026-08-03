package com.soldesk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.MemberMapper;
import com.soldesk.vo.MemberVO;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final MemberMapper memberMapper;

    public AdminService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public void approveMember(int memberId) {
        logger.info("학교 인증 승인 요청: memberId={}", memberId);
        memberMapper.approveMember(memberId);
    }

    public void rejectMember(int memberId) {
        logger.info("학교 인증 반려 요청: memberId={}", memberId);
        memberMapper.rejectMember(memberId);
    }

    @Transactional
    public void suspendMember(int memberId) {
        MemberVO member = memberMapper.getMemberById((long) memberId);
        if (member == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }
        if ("ADMIN".equalsIgnoreCase(member.getRole())) {
            throw new IllegalArgumentException("관리자 계정은 정지할 수 없습니다.");
        }
        logger.info("계정 정지: memberId={}", memberId);
        memberMapper.updateAccountStatus(memberId, "SUSPENDED");
    }

    @Transactional
    public void resumeMember(int memberId) {
        MemberVO member = memberMapper.getMemberById((long) memberId);
        if (member == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }
        if ("WITHDRAWN".equalsIgnoreCase(member.getAccount_status())) {
            throw new IllegalArgumentException("탈퇴한 계정은 복구할 수 없습니다.");
        }
        logger.info("계정 정지 해제: memberId={}", memberId);
        memberMapper.updateAccountStatus(memberId, "ACTIVE");
    }
}
