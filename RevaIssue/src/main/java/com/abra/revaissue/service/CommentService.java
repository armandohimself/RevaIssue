package com.abra.revaissue.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.abra.revaissue.dto.CommentDTO;
import com.abra.revaissue.entity.Comment;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.EntityType;
import com.abra.revaissue.repository.CommentRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for managing Comment entities.
 * Provides methods to add, delete, and retrieve comments.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final LogTransactionService logTransactionService;

    /**
     * Constructor injection for CommentRepository and LogTransactionService.
     *
     * @param commentRepository     repository for Comment entities
     * @param logTransactionService service for logging transactions
     */

    public CommentService(CommentRepository commentRepository, LogTransactionService logTransactionService) {
        this.commentRepository = commentRepository;
        this.logTransactionService = logTransactionService;
    }

    /**
     * Saves a new comment to the database.
     *
     * @param comment    the Comment entity to save
     * @param actingUser the user performing the action
     * @return the saved Comment entity
     */
    public Comment addComment(Comment comment, User actingUser) {
        Comment savedComment = commentRepository.save(comment);
        logTransactionService.logAction("Comment added", actingUser, EntityType.COMMENT, savedComment.getCommentId());
        return savedComment;
    }

    public Comment getCommentById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    /**
     * Deletes a comment by its UUID.
     *
     * @param commentId  the UUID of the comment to delete
     * @param actingUser the user performing the deletion
     * @return true if the comment existed and was deleted, false otherwise
     */
    public boolean deleteComment(UUID commentId, User actingUser) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            logTransactionService.logAction("Comment deleted", actingUser, EntityType.COMMENT, commentId);
            return true;
        }
        return false;
    }

    /**
     * Retrieves all comments in the system.
     *
     * @return a list of all CommentDTOs
     */
    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll().stream()
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getMessage(),
                        comment.getTime(),
                        comment.getUser().getUserName()))
                .toList();
    }

    /**
     * Retrieves a paginated list of comments for a specific issue.
     *
     * @param issueId  the UUID of the issue
     * @param pageable pagination and sorting information
     * @return a Page of CommentDTOs for the given issue
     */
    public Page<CommentDTO> getCommentsByIssueId(UUID issueId, Pageable pageable) {
        return commentRepository.findByIssue_IssueId(issueId, pageable)
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getMessage(),
                        comment.getTime(),
                        comment.getUser().getUserName()));
    }

    /**
     * Retrieves a paginated list of comments made by a specific user.
     *
     * @param userId   the UUID of the user
     * @param pageable pagination and sorting information
     * @return a Page of CommentDTOs for the given user
     */
    public Page<CommentDTO> getCommentsByUserId(UUID userId, Pageable pageable) {
        return commentRepository.findByUser_UserId(userId, pageable)
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getMessage(),
                        comment.getTime(),
                        comment.getUser().getUserName()));
    }

}
