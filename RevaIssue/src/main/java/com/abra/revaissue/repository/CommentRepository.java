package com.abra.revaissue.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abra.revaissue.entity.Comment;

/**
 * Repository interface for performing CRUD operations on Comment entities.
 * Extends JpaRepository to leverage Spring Data JPA functionalities.
 * Provides methods to find comments by user ID or issue ID.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    /**
     * Retrieves a paginated set of comments for a specific issue.
     * Useful when there are many comments and you want to load them in pages.
     *
     * @param issueId  the UUID of the issue
     * @param pageable pagination and sorting information
     * @return a Page containing Comment entities for the given issue
     */
    Page<Comment> findByIssue_IssueId(UUID issueId, Pageable pageable);

    /**
     * Retrieves a paginated set of comments made by a specific user.
     * Added here in case the method is needed.
     * It is used in the actual application.
     *
     * @param userId   the UUID of the user
     * @param pageable pagination and sorting information
     * @return a Page containing Comment entities made by the user
     */
    Page<Comment> findByUser_UserId(UUID userId, Pageable pageable);

}