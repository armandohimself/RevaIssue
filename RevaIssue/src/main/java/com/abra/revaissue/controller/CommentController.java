package com.abra.revaissue.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.abra.revaissue.service.CommentService;
import com.abra.revaissue.service.IssueService;
import com.abra.revaissue.service.JwtService;
import com.abra.revaissue.service.UserService;

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

    @PostMapping
    public CommentDTO createComment(@RequestBody CommentRequestDTO request,
            @RequestHeader("Authorization") String authorization) {
        // Fetch User and Issue entities
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        String token = authorization.substring(7);
        UUID userId = jwtService.getUserIdFromToken(token);
        User user = userService.getUserByUUID(userId);
        Issue issue = issueService.getIssueEntityById(request.issueId());
        // Build Comment entity
        Comment newComment = new Comment();
        newComment.setMessage(request.message());
        newComment.setUser(user);
        newComment.setIssue(issue);

        // Save using the service
        Comment savedComment = commentService.addComment(newComment);

        // Convert to DTO and return
        return new CommentDTO(
                savedComment.getCommentId(),
                savedComment.getMessage(),
                savedComment.getTime(),
                savedComment.getUser().getUserName());
    }

    // Front end also passes in "?page=0&size=10&sort=time,desc" that is
    // automatically populated into Pageable
    @GetMapping("/issue/{issueId}")
    public Page<CommentDTO> getCommentsByIssue(
            @PathVariable UUID issueId,
            @PageableDefault(size = 10, sort = "time,asc") Pageable pageable) {
        return commentService.getCommentsByIssueId(issueId, pageable);
    }

    @GetMapping("/user/{userId}")
    public Iterable<CommentDTO> getCommentsByUser(@PathVariable UUID userId) {
        return commentService.getCommentsByUserId(userId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
    }
}
