package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.CommentVO;


public interface CommentMapper {

    void addComment(CommentVO comment);
    List<CommentVO> getCommentsByProjectId(Long projectId);
    void updateComment(CommentVO comment);
    void deleteComment(Long comment_id);
    void deleteCommentsByProjectId(Long projectId);
    void addTalentComment(CommentVO comment);
    void deleteTalentCommentsByPostId(Long postId);
    List<CommentVO> getCommentsByPostId(Long postId);
}
