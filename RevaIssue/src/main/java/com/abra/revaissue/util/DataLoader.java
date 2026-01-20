package com.abra.revaissue.util;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.abra.revaissue.entity.Comment;
import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.enums.IssueStatus;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.CommentRepository;
import com.abra.revaissue.repository.IssueRepository;
import com.abra.revaissue.enums.ProjectStatus;
import com.abra.revaissue.repository.ProjectRepository;
import com.abra.revaissue.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void preload() {
        User admin = preloadAdmin();
        preloadProjects(admin);
        preloadApiTestData();
    }

    /**
     * Ensures an admin exists and returns it.
     */
    private User preloadAdmin() {
        User admin = userRepository.findByUserName("admin");
        if (admin == null) {
            admin = new User();
            admin.setUserName("admin");
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            admin = userRepository.save(admin);
        }
        User tester = userRepository.findByUserName("tester");
        if (tester == null) {
            tester = new User();
            tester.setUserName("tester");
            tester.setPasswordHash(passwordEncoder.encode("password"));
            tester.setRole(Role.TESTER);
            tester = userRepository.save(tester);
        }
        User developer = userRepository.findByUserName("developer");
        if (developer == null) {
            developer = new User();
            developer.setUserName("developer");
            developer.setPasswordHash(passwordEncoder.encode("password"));
            developer.setRole(Role.DEVELOPER);
            developer = userRepository.save(developer);
        }
        Project project = new Project();
        project.setProjectName("Default Project");
        project.setProjectDescription("This is a default project.");
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());
        project.setCreatedByUserId(admin.getUserId());
        Project savedProject = projectRepository.save(project);
        Issue issue = new Issue();
        issue.setName("Sample Issue");
        issue.setDescription("This is a sample issue for data loading.");
        issue.setStatus(IssueStatus.OPEN);
        issue.setSeverity(IssueSeverity.LOW);
        issue.setPriority(IssuePriority.LOW);
        issue.setProject(savedProject);
        issue.setCreatedBy(admin);
        issue.setCreatedAt(Instant.now());
        Issue savedIssue = issueRepository.save(issue);
        for (int i = 1; i <= 15; i++) {
            Comment comment = new Comment();
            comment.setMessage("Sample comment " + i);
            comment.setIssue(savedIssue);
            comment.setUser(admin);
            commentRepository.save(comment);
        }
        System.out.println("Issue ID: " + savedIssue.getIssueId().toString());
        return admin;
    }

    /**
     * Seeds a bunch of projects (only if there are none yet).
     */
    private void preloadProjects(User admin) {
        // Don’t spam duplicates every restart.
        if (projectRepository.count() > 0) {
            return;
        }

        UUID adminId = admin.getUserId();

        Instant start = Instant.now().minus(Duration.ofDays(90));
        int i = 0;

        List<ProjectSeed> seeds = List.of(
                new ProjectSeed("RevaIssue", "Internal issue tracker for training team", ProjectStatus.ACTIVE),
                new ProjectSeed("Project Atlas", "Roadmap + milestones for platform revamp", ProjectStatus.ACTIVE),
                new ProjectSeed("Client Portal", "Customer login + billing history", ProjectStatus.ACTIVE),
                new ProjectSeed("Ops Dashboard", "KPIs + health metrics for operations", ProjectStatus.ACTIVE),
                new ProjectSeed("Auth Refactor", "Consolidate auth flows and roles", ProjectStatus.ACTIVE),
                new ProjectSeed("SQLite Migration POC", "Experiment with SQLite persistence rules",
                        ProjectStatus.ARCHIVED),
                new ProjectSeed("Notification Service", "Email + in-app notifications", ProjectStatus.ACTIVE),
                new ProjectSeed("Angular UI Kit", "Reusable components + theming", ProjectStatus.ACTIVE),
                new ProjectSeed("API Gateway POC", "Proxy + routing rules for services", ProjectStatus.ARCHIVED),
                new ProjectSeed("Permissions Matrix", "Role-based access control mapping", ProjectStatus.ACTIVE),
                new ProjectSeed("Onboarding Wizard", "Guided first-time user setup", ProjectStatus.ACTIVE),
                new ProjectSeed("Report Builder", "Create and export dynamic reports", ProjectStatus.ACTIVE),
                new ProjectSeed("Search & Filter", "Fast search across projects/issues", ProjectStatus.ACTIVE),
                new ProjectSeed("Audit Log v1", "Track who changed what and when", ProjectStatus.ACTIVE),
                new ProjectSeed("Status Workflow", "ACTIVE → ARCHIVED transitions + rules", ProjectStatus.ACTIVE),
                new ProjectSeed("Performance Sprint", "Bundle size + API latency improvements", ProjectStatus.ACTIVE),
                new ProjectSeed("Legacy Cleanup", "Remove dead code paths", ProjectStatus.ARCHIVED),
                new ProjectSeed("Mobile Layout", "Responsive UI for smaller screens", ProjectStatus.ACTIVE),
                new ProjectSeed("Accessibility Pass", "Keyboard nav + ARIA improvements", ProjectStatus.ACTIVE),
                new ProjectSeed("Data Import Tool", "CSV import for project metadata", ProjectStatus.ACTIVE),
                new ProjectSeed("Demo Sandbox", "Safe testing environment for new devs", ProjectStatus.ACTIVE),
                new ProjectSeed("Billing Integration", "Stripe billing integration proof", ProjectStatus.ARCHIVED),
                new ProjectSeed("Team Directory", "Search users + roles", ProjectStatus.ACTIVE),
                new ProjectSeed("Release Notes", "Automated changelog generation", ProjectStatus.ACTIVE));

        // Convert seeds -> entities and save
        List<Project> toSave = new ArrayList<>();

        for (ProjectSeed seed : seeds) {
            Instant createdAt = start.plus(Duration.ofDays(i));
            Instant updatedAt = createdAt.plus(Duration.ofHours(2));

            Project p = new Project();
            p.setProjectName(seed.projectName);
            p.setProjectDescription(seed.projectDescription);
            p.setProjectStatus(seed.projectStatus);

            // audit fields
            p.setCreatedByUserId(adminId);
            p.setCreatedAt(createdAt);
            p.setUpdatedAt(updatedAt);

            p.setStatusUpdatedByUserId(adminId);

            if (seed.projectStatus == ProjectStatus.ARCHIVED) {
                p.setArchivedByUserId(adminId);
                p.setArchivedAt(updatedAt);
            }

            toSave.add(p);
            i++;
        }

        projectRepository.saveAll(toSave);
    }

    /**
     * Tiny helper record so seeds are readable.
     */
    private static class ProjectSeed {
        final String projectName;
        final String projectDescription;
        final ProjectStatus projectStatus;

        ProjectSeed(String projectName, String projectDescription, ProjectStatus projectStatus) {
            this.projectName = projectName;
            this.projectDescription = projectDescription;
            this.projectStatus = projectStatus;
        }

    }

    private void preloadApiTestData(){
        User admin = addUser("apiadmin", "password", Role.ADMIN);
        User tester = addUser("apitester", "password", Role.TESTER);
        User dev1 = addUser("apidev1", "password", Role.DEVELOPER);
        User dev2 = addUser("apidev2", "password", Role.DEVELOPER);

        Project apiProject = new Project();
        apiProject.setProjectName("API Test Project");
        apiProject.setProjectDescription("Seeded for API integration tests");
        apiProject.setProjectStatus(ProjectStatus.ACTIVE);

        apiProject.setCreatedByUserId(admin.getUserId());
        apiProject.setStatusUpdatedByUserId(admin.getUserId());
        apiProject.setCreatedAt(Instant.now());
        apiProject.setUpdatedAt(Instant.now());
        apiProject = projectRepository.save(apiProject);

        addIssue(apiProject, tester, dev1,"API Issue Open Low", "OPEN/LOW/LOW", IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);
        addIssue(apiProject, tester, dev2,"API Issue Open High", "OPEN/HIGH/HIGH", IssueStatus.OPEN, IssueSeverity.HIGH, IssuePriority.HIGH);
        addIssue(apiProject, admin, dev1,"API Issue Closed Medium", "CLOSED/MEDIUM/MEDIUM", IssueStatus.CLOSED, IssueSeverity.MEDIUM, IssuePriority.MEDIUM);
        addIssue(apiProject, admin, dev2,"API Issue Resolved High", "RESOLVED/HIGH/MEDIUM", IssueStatus.RESOLVED, IssueSeverity.HIGH, IssuePriority.MEDIUM);
    }

    private User addUser(String username, String password, Role role){
        User user = userRepository.findByUserName(username);
         if (user == null) {
            user = new User();
            user.setUserName(username);
            user.setPasswordHash(password);
            user.setRole(role);
            user = userRepository.save(user);
        }
        return user;
    }

    private void addIssue(Project project, User createdBy, User assignedTo, String name, String description, IssueStatus status, IssueSeverity severity, IssuePriority priority){
        Issue issue = new Issue();
        issue.setName(name);
        issue.setDescription(description);
        issue.setStatus(status);
        issue.setSeverity(severity);
        issue.setPriority(priority);
        issue.setProject(project);
        issue.setCreatedBy(createdBy);
        issue.setAssignedTo(assignedTo);
        issue.setCreatedAt(Instant.now());
        issue.setUpdatedAt(Instant.now());
        issueRepository.save(issue);
    }
}
