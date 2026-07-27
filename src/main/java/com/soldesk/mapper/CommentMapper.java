package com.soldesk.mapper;

import java.util.List;

import com.soldesk.vo.CommentVO;


public interface CommentMapper {

    void addComment(CommentVO comment);
    List<CommentVO> getCommentsByProjectId(Long projectId);
    void deleteComment(Long commentId);

}
