package com.abra.revaissue.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.abra.revaissue.entity.ProjectMember;
import com.abra.revaissue.enums.ProjectRole;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    List<ProjectMember> findByProjectId(UUID projectId);

    List<ProjectMember> findByProjectIdAndRemovedAtIsNull(UUID projectId);

    // List<ProjectMember> findByProjectIdAndRemovedAtIsNotNull(UUID projectId);

    List<ProjectMember> findByProjectIdAndProjectRoleAndRemovedAtIsNull(UUID projectId, ProjectRole projectRole);

    Optional<ProjectMember> findByProjectIdAndUserIdAndRemovedAtIsNull(UUID projectId, UUID userId);

    boolean existsByProjectIdAndUserIdAndRemovedAtIsNull(UUID projectId, UUID userId);

    List<ProjectMember> findByUserIdAndRemovedAtIsNull(UUID userId);
}