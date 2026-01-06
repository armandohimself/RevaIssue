package com.abra.revaissue.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.abra.revaissue.entity.ProjectAccess;
import com.abra.revaissue.enums.ProjectRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectAccessRepository extends JpaRepository<ProjectAccess, UUID> {
    List<ProjectAccess> findByProjectIdIn(UUID projectId);

    List<ProjectAccess> findByProjectIdAndRevokedAccessAtIsNull(UUID projectId);

    // List<ProjectAccess> findByProjectIdAndRevokedAccessAtIsNotNull(UUID projectId);

    List<ProjectAccess> findByProjectIdAndProjectRoleAndRevokedAccessAtIsNull(UUID projectId, ProjectRole projectRole);

    Optional<ProjectAccess> findByProjectIdAndUserIdAndRevokedAccessAtIsNull(UUID projectId, UUID userId);

    boolean existsByProjectIdAndUserIdAndRevokedAccessAtIsNull(UUID projectId, UUID userId);

    List<ProjectAccess> findByUserIdAndRevokedAccessAtIsNull(UUID userId);
}
