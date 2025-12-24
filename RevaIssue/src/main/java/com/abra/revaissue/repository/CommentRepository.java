package com.abra.revaissue.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abra.revaissue.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // Find all comments by a userId
    List<Comment> findByUser_UserId(UUID userId);

    // Find all comments for an issue by issueId
    List<Comment> findByIssue_IssueId(UUID issueId);

    // Find all comments for an issue ordered by time ascending (oldest first)
    List<Comment> findByIssue_IssueIdOrderByTimeAsc(UUID issueId);

    // Find all comments for an issue ordered by time descending (newest first)
    List<Comment> findByIssue_IssueIdOrderByTimeDesc(UUID issueId);

    // Paginated comments for an issue (if we need it)
    Page<Comment> findByIssue_IssueId(UUID issueId, Pageable pageable);

}