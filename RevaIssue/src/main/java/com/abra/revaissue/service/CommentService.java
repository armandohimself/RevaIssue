package com.abra.revaissue.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.abra.revaissue.dto.CommentDTO;
import com.abra.revaissue.entity.Comment;
import com.abra.revaissue.repository.CommentRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    // Better for testing purposes compared to field injection
    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // Save a new comment
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Delete a comment by ID
    public boolean deleteComment(UUID commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }

    // Probably not needed
    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll().stream()
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getMessage(),
                        comment.getTime(),
                        comment.getUser().getUserName()))
                .toList();
    }

    // Fetch all comments by a user
    public List<CommentDTO> getCommentsByUserId(UUID userId) {
        return commentRepository.findByUser_UserId(userId).stream()
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getMessage(),
                        comment.getTime(),
                        comment.getUser().getUserName()))
                .toList();
    }

    // Fetch all comments for an issue
    public List<CommentDTO> getCommentsByIssueId(UUID issueId) {
        return commentRepository.findByIssue_IssueId(issueId).stream()
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getMessage(),
                        comment.getTime(),
                        comment.getUser().getUserName()))
                .toList();
    }

    // Fetch all comments for an issue, sorted by oldest first
    public List<CommentDTO> getCommentsByIssueIdOldestFirst(UUID issueId) {
        return commentRepository.findByIssue_IssueIdOrderByTimeAsc(issueId).stream()
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getMessage(),
                        comment.getTime(),
                        comment.getUser().getUserName()))
                .toList();
    }

    // Fetch all comments for an issue, sorted by newest first
    public List<CommentDTO> getCommentsByIssueIdNewestFirst(UUID issueId) {
        return commentRepository.findByIssue_IssueIdOrderByTimeDesc(issueId).stream()
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getMessage(),
                        comment.getTime(),
                        comment.getUser().getUserName()))
                .toList();
    }

    // Fetch paginated comments for an issue (if we need it)
    public Page<CommentDTO> getCommentsByIssueId(UUID issueId, Pageable pageable) {
        return commentRepository.findByIssue_IssueId(issueId, pageable)
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getMessage(),
                        comment.getTime(),
                        comment.getUser().getUserName()));
    }

}