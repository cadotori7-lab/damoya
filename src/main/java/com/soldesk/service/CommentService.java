package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.CommentMapper;
import com.soldesk.vo.CommentVO;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Transactional
    public void addComment(CommentVO comment) {
        commentMapper.addComment(comment);
    }
    @Transactional
    public List<CommentVO> getCommentsByProjectId(Long projectId) {
        return commentMapper.getCommentsByProjectId(projectId);
    }
    @Transactional
    public void deleteComment(Long commentId) {
        commentMapper.deleteComment(commentId);
    }

}
