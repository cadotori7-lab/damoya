package com.soldesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk.mapper.CommentMapper;
import com.soldesk.vo.CommentVO;
import com.soldesk.vo.ProjectVO;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectService projectService;

    @Transactional
    public void addComment(CommentVO comment) {
        ProjectVO project = projectService.getProjectById(comment.getProject_id());
        if (project != null) {
            int ownerId = project.getOwnerId().intValue();
            notificationService.toMessage(comment.getProject_id(), ownerId, "SYSTEM", project.getTitle());
        }
        commentMapper.addComment(comment);
    }
    @Transactional
    public List<CommentVO> getCommentsByProjectId(Long projectId) {
        return commentMapper.getCommentsByProjectId(projectId);
    }
    @Transactional
    public void updateComment(CommentVO comment) {
        commentMapper.updateComment(comment);
    }
    @Transactional
    public void deleteComment(Long comment_id) {
        commentMapper.deleteComment(comment_id);
    }
    @Transactional
    public void deleteCommentsByProjectId(Long projectId) {
        commentMapper.deleteCommentsByProjectId(projectId);
    }
    @Transactional
    public void addTalentComment(CommentVO comment) {
        commentMapper.addTalentComment(comment);
    }
    @Transactional
    public void deleteTalentCommentsByPostId(Long postId) {
        commentMapper.deleteTalentCommentsByPostId(postId);
    }
    @Transactional
    public List<CommentVO> getCommentsByPostId(Long postId) {
        return commentMapper.getCommentsByPostId(postId);
    }

}
