package com.abra.revaissue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abra.revaissue.entity.Comment;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByIssue_IssueId(UUID issueId);

    List<Comment> findAllByUser_UserId(UUID userId);
}
