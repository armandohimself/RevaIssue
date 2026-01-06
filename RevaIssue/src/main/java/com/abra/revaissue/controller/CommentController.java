package com.abra.revaissue.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abra.revaissue.dto.CommentDTO;
import com.abra.revaissue.dto.CommentRequestDTO;
import com.abra.revaissue.entity.Comment;
import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.service.CommentService;
import com.abra.revaissue.service.IssueService;
import com.abra.revaissue.service.JwtService;
import com.abra.revaissue.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final IssueService issueService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public CommentController(CommentService commentService, IssueService issueService, UserService userService,
            JwtService jwtService) {
        this.commentService = commentService;
        this.issueService = issueService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Creates a new comment.
     *
     * @param request       DTO containing message and issueId
     * @param authorization JWT Bearer token header
     * @return ResponseEntity with the created CommentDTO and 201 Created status
     */
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentRequestDTO request,
            @RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorization.substring(7);
        if (jwtService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = jwtService.getUserIdFromToken(token);
        User user = userService.getUserByUUID(userId);
        Issue issue = issueService.getIssueEntityById(request.issueId());

        Comment newComment = new Comment();
        newComment.setMessage(request.message());
        newComment.setUser(user);
        newComment.setIssue(issue);

        Comment savedComment = commentService.addComment(newComment, user);

        CommentDTO newCommentDTO = new CommentDTO(
                savedComment.getCommentId(),
                savedComment.getMessage(),
                savedComment.getTime(),
                savedComment.getUser().getUserName());

        return ResponseEntity.created(URI.create("/comments/" + newCommentDTO.commentId())).body(newCommentDTO);
    }

    /**
     * Retrieves paginated comments for a specific issue.
     *
     * @param issueId  the UUID of the issue
     * @param pageable pagination and sorting info
     * @return ResponseEntity containing Page of CommentDTOs
     */
    @GetMapping("/issue/{issueId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByIssue(
            @PathVariable UUID issueId,
            Pageable pageable,
            @RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorization.substring(7);
        if (jwtService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Page<CommentDTO> comments = commentService.getCommentsByIssueId(issueId, pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Retrieves comments made by a specific user.
     *
     * @param userId   the UUID of the user
     * @param pageable pagination and sorting info
     * @return ResponseEntity containing Page of CommentDTOs
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByUser(
            @PathVariable UUID userId,
            Pageable pageable, @RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorization.substring(7);
        if (jwtService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Page<CommentDTO> comments = commentService.getCommentsByUserId(userId, pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Deletes a comment by its UUID.
     *
     * @param commentId the UUID of the comment
     * @return ResponseEntity with 204 No Content if deleted, or 404 Not Found
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable UUID commentId,
            @RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorization.substring(7);
        if (jwtService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID tokenUserId = jwtService.getUserIdFromToken(token);
        User user = userService.getUserByUUID(tokenUserId);
        Role role = jwtService.getUserRoleFromToken(token);
        Comment comment;
        try {
            comment = commentService.getCommentById(commentId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        UUID commentOwnerId = comment.getUser().getUserId();
        boolean isOwner = commentOwnerId.equals(tokenUserId);
        boolean isAdmin = role == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }
}
