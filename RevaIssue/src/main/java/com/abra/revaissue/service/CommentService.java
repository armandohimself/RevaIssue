package com.abra.revaissue.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abra.revaissue.entity.Comment;
import com.abra.revaissue.repository.CommentRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByIssueId(UUID issueId) {
        return commentRepository.findAllByIssueId(issueId);
    }

    public List<Comment> getCommentsByUserId(UUID userId) {
        return commentRepository.findAllByUserId(userId);
    }
}