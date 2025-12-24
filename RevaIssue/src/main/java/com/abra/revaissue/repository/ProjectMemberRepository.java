package com.abra.revaissue.repository;
/**
 * ? What is a Repository
 * JARGON: Spring Data JPA generates a runtime implementation (proxy) of your repository interface.
 * ENGLISH: Repository is a “database helper” Spring gives you so you don’t write SQL for basic stuff.
 * 
 * * Why SQLite & DBeaver?
 * JARGON: SQLite is your persistence engine.
 * ENGLISH: SQLite is the actual database storing data.
 * 
 * JARGON: DBeaver is a DB client/GUI for inspecting schema + running SQL
 * ENGLISH: DBeaver is just a viewer + query tool.
 * 
 * 
 * 
 */

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.abra.revaissue.entity.ProjectMember;
import com.abra.revaissue.enums.ProjectRole;

// Repository is a “database helper” Spring gives you so you don’t write SQL for basic stuff.
import org.springframework.data.jpa.repository.JpaRepository;

// interface is a “promise” of what methods exist, without writing the method bodies.
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    /**
     * ? public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID>
     * * interface
     * JARGON: An interface defines a contract (method signatures) with no implementation.
     * ENGLISH: interface is a “promise” of what methods exist, without writing the method bodies.
     * 
     * * extends JpaRepository
     * 
     * * <ProjectMember, UUID>
     * First Generic tells us what type of Entity we are dealing with
     * Second Generic tells us what type of id type we are using: UUID in this case
     * 
     * * if we implemented class here?
     * Then Spring wouldn't do shit for us, no contract made. 
     * We would HAVE to implement everything ourselves
     * 
     * * Common building words you’ll reuse
     * - findBy...
     * - existsBy...
     * - countBy...
     * - And / Or
     * - IsNull / IsNotNull
     * - In (list membership)
     * - Between (time ranges)
     * - OrderBy...Asc/Desc
     * 
     * * Current Properties in ProjectMemberRepository
     * UUID projectMemberId; NOT NULL
     * UUID projectId; NOT NULL
     * UUID userId; NOT NULL
     * ProjectRole projectRole; NOT NULL
     * UUID assignedByUserId; NOT NULL
     * Instant memberAssignedAt; NOT NULL
     * UUID removedByUserId;
     * Instant removedAt;
     */

    /**
     * ? Find me a list of ALL project members (active or not) on THIS project
     * * WHY: Useful for the AUDIT HISTORY screens
     * 
     * @param projectId
     * @return
     */
    List<ProjectMember> findByProjectId(UUID projectId);
    /**
     * * WHY/WHAT List: ordered collection (keeps insertion order) usually backed by ArrayList
     * FEATURES:
     * - allows duplicates, not a set
     * - can access index using list.get[0]
     * 
     * * Common Methods
     * - add(item)
     * - get(index)
     * - size()
     * - isEmpty()
     * - remove(...)
     * - contains(...)
     * - forEach(...)
     * 
     * * Other Data Types We Could Have Used
     * - Set = no duplicates (useful if you need uniqueness)
     * - Map = key → value lookup
     * - Optional = maybe-one (safe single return)
     * 
     */

    /**
     * ? Find me a list of ALL the ACTIVE project members on THIS projectId
     * @param projectId
     * @return
     */
    List<ProjectMember> findByProjectIdAndRemovedAtIsNull(UUID projectId);

    /**
     * ? Find me a list of ALL the NON-ACTIVE project members on THIS projectId
     * TODO: Is is possible to add dynamic queries in our method names?
     * EX: IsNotNull vs IsNull
     * @param projectId
     * @return
     */
    //List<ProjectMember> findByProjectIdAndRemovedAtIsNotNull(UUID projectId);

    /**
     * ? Find me the list of ALL the ACTIVE project members on THIS projectId BASED on THIS PROJECT ROLE
     * 
     * Ideal for looking up testers and developers, then later PROJECT_ADMINS if needed
     * * WHY: Helps enforce ROLE rules and UI FILTERS 
     * @param projectId
     * @param projectRole
     * @return
     */
    List<ProjectMember> findByProjectIdAndProjectRoleAndRemovedAtIsNull(UUID projectId, ProjectRole projectRole);

    /**
     * ? Find me THIS specific userId for THIS specific projectId
     * * WHY: Needed when removing a member, changing role, seeing who assigned this guy to this project, at what time, etc
     * @param projectId
     * @param userId
     * @return
    */
    Optional<ProjectMember> findByProjectIdAndUserIdAndRemovedAtIsNull(UUID projectId, UUID userId);
    /**
    * * WHY & What is Optional: Optional means "maybe exists, maybe not" (prevents null bugs).
     * 
     */

    /**
     * ? Is THIS userId ACTIVELY on THIS projectId
     * * WHY: Helps prevent duplicate entries
     * @param projectId
     * @param userId
     * @return
     */
    Boolean existsByProjectIdAndUserIdAndRemovedAtIsNull(UUID projectId, UUID userId);

    /**
     * ? What projects is THIS userId assigned to?
     * * WHY: Perfect for a my projects UI view
     * @param userId
     * @return
     */
    List<ProjectMember> findByUserIdAndRemovedAtIsNull(UUID userId);

    /**
     * CONSIDER ADDING:
     * Useful but “later”
     * - Pagination -> Page<ProjectMember> findByProjectId(..., Pageable pageable)
     * - Sorting -> add OrderByMemberAssignedAtDesc
     * - Complex audit reporting -> custom SQL / @Query
     * 
     * Out of scope (for now)
     * - Full-blown “permission engine”
     * - Complex reporting with joins across many tables
     * - Multi-tenant filtering
     */

   
    

}