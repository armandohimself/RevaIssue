package com.abra.revaissue.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.abra.revaissue.dto.CommentDTO;
import com.abra.revaissue.entity.Comment;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.EntityType;
import com.abra.revaissue.repository.CommentRepository;
import com.abra.revaissue.service.CommentService;
import com.abra.revaissue.service.LogTransactionService;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LogTransactionService logTransactionService;

    @InjectMocks
    private CommentService commentService;

    private User actingUser;
    private Comment comment;

    @BeforeEach
    void setUp() {
        // Initialization before each test if needed
        actingUser = new User();
        actingUser.setUserName("admin");
        comment = new Comment();
        comment.setCommentId(UUID.randomUUID());
        comment.setMessage("This is a test comment.");
        comment.setUser(actingUser);
    }

    @Test
    void testAddComment() {
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = commentService.addComment(comment, actingUser);

        assertEquals(comment, result);
        verify(commentRepository).save(comment);
        verify(logTransactionService).logAction("Comment added", actingUser, EntityType.COMMENT,
                comment.getCommentId());
    }

    @Test
    void testGetCommentsByIssueId() {
        UUID issueId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Comment randomComment = new Comment();
        randomComment.setCommentId(UUID.randomUUID());
        randomComment.setMessage("This is a test comment.");
        randomComment.setUser(actingUser);
        Page<Comment> commentPage = new PageImpl<>(List.of(randomComment));
        when(commentRepository.findByIssue_IssueId(issueId, pageable)).thenReturn(commentPage);

        Page<CommentDTO> result = commentService.getCommentsByIssueId(issueId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(randomComment.getMessage(), result.getContent().get(0).message());
        assertEquals(randomComment.getUser().getUserName(), result.getContent().get(0).username());
        verify(commentRepository).findByIssue_IssueId(issueId, pageable);
    }
}
