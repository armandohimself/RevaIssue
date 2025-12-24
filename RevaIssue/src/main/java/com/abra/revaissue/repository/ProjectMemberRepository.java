package com.abra.revaissue.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.ProjectMember;
import com.abra.revaissue.enums.ProjectRole;

// Repository is a “database helper” Spring gives you so you don’t write SQL for basic stuff.
import org.springframework.data.jpa.repository.JpaRepository;

// interface is a “promise” of what methods exist, without writing the method bodies.
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {

    // Find me a list of all these specific projects
    List<ProjectMember> findByProjectId(UUID projectId);

    // Find me a list of people who are active on a given project
    List<ProjectMember> findByProjectIdAndRemovedAtIsNull(UUID projectId);

    // Find me the list of people who are active on a given project based on their role
    List<ProjectMember> findByProjectIdAndProjectRoleAndRemovedAtIsNull(UUID projectId, ProjectRole projectRole);

    // Find me THIS specific project for THIS specific user 
    Optional<ProjectMember> findByProjectIdAndUserIdAndRemovedAtIsNull(UUID projectId, UUID userId);



    // query who is on the project

    // query who 


    // UUID projectMemberId;
    // UUID projectId;
    // UUID userId;
    // ProjectRole projectRole;
    // UUID assignedByUserId;
    // Instant memberAssignedAt;
    // UUID removedByUserId;
    // Instant removedAt;
    

}