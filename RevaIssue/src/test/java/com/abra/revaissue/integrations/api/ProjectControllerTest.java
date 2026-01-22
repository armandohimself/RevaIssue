package com.abra.revaissue.integrations.api;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.abra.revaissue.controller.ProjectController;
import com.abra.revaissue.dto.project.CreateProjectRequest;
import com.abra.revaissue.dto.project.UpdateProjectRequest;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.enums.ProjectStatus;
import com.abra.revaissue.service.AuthzService;
import com.abra.revaissue.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false) // prevents Spring Security from auto-blocking MVC tests (if present)
class ProjectControllerTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean private ProjectService projectService;
    @MockitoBean private AuthzService authzService;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_TOKEN = "Bearer test.jwt.token";

    private static Project project(UUID projectId, String name, String desc, ProjectStatus status, UUID actingUserId) {
        Project p = new Project();
        p.setProjectId(projectId);
        p.setProjectName(name);
        p.setProjectDescription(desc);
        p.setProjectStatus(status);

        p.setCreatedByUserId(actingUserId);
        p.setStatusUpdatedByUserId(null);
        p.setArchivedByUserId(null);

        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        p.setCreatedAt(now);
        p.setUpdatedAt(now);
        p.setArchivedAt(null);

        return p;
    }

    // -----------------------
    // POST /api/projects
    // -----------------------

    @Test
    void create_returns_200_and_ProjectResponse_json() throws Exception {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(authzService.actingUserId(BEARER_TOKEN)).thenReturn(actingUserId);

        Project saved = project(projectId, "QA Testing Project", "Created by controller test", ProjectStatus.ACTIVE, actingUserId);
        when(projectService.create(any(Project.class), eq(actingUserId))).thenReturn(saved);

        CreateProjectRequest req = new CreateProjectRequest("QA Testing Project", "Created by controller test");

        mockMvc.perform(
                post("/api/projects")
                    .header(AUTH_HEADER, BEARER_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId.toString()))
            .andExpect(jsonPath("$.projectName").value("QA Testing Project"))
            .andExpect(jsonPath("$.projectDescription").value("Created by controller test"))
            .andExpect(jsonPath("$.projectStatus").value("ACTIVE"))
            .andExpect(jsonPath("$.createdByUserId").value(actingUserId.toString()))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());

        verify(authzService).actingUserId(BEARER_TOKEN);
        verify(projectService).create(any(Project.class), eq(actingUserId));
    }

    @Test
    void create_missing_auth_header_returns_400() throws Exception {
        CreateProjectRequest req = new CreateProjectRequest("X", "Y");

        mockMvc.perform(
                post("/api/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isBadRequest()); // missing required @RequestHeader("Authorization")
    }

    // -----------------------
    // GET /api/projects
    // -----------------------

    @Test
    void listAll_returns_200_and_array() throws Exception {
        UUID actingUserId = UUID.randomUUID();

        // NOTE: Your controller signature requires the header, but does not use it.
        // We don't need to stub authzService.actingUserId for this endpoint.
        Project p1 = project(UUID.randomUUID(), "Default Project", "Seeded", ProjectStatus.ACTIVE, actingUserId);
        Project p2 = project(UUID.randomUUID(), "Archived One", "Still listed", ProjectStatus.ARCHIVED, actingUserId);

        when(projectService.getAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(
                get("/api/projects")
                    .header(AUTH_HEADER, BEARER_TOKEN)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].projectName").value("Default Project"))
            .andExpect(jsonPath("$[1].projectStatus").value("ARCHIVED"));

        verify(projectService).getAll();
    }

    @Test
    void listAll_missing_auth_header_returns_400() throws Exception {
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isBadRequest());
    }

    // -----------------------
    // GET /api/projects/{id}
    // -----------------------

    @Test
    void getById_returns_200_and_ProjectResponse_json() throws Exception {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project p = project(projectId, "Get Me", "By id", ProjectStatus.ACTIVE, actingUserId);
        when(projectService.getById(projectId)).thenReturn(p);

        mockMvc.perform(
                get("/api/projects/{projectId}", projectId)
                    .header(AUTH_HEADER, BEARER_TOKEN)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId.toString()))
            .andExpect(jsonPath("$.projectName").value("Get Me"));

        verify(projectService).getById(projectId);
    }

    // -----------------------
    // GET /api/projects/{id}/admin
    // -----------------------

    @Test
    void getAdminProject_calls_mustBeAdmin_and_returns_AdminProjectResponse() throws Exception {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(authzService.actingUserId(BEARER_TOKEN)).thenReturn(actingUserId);
        doNothing().when(authzService).mustBeAdmin(actingUserId);

        Project p = project(projectId, "Admin View", "audit view", ProjectStatus.ACTIVE, actingUserId);
        when(projectService.getById(projectId)).thenReturn(p);

        mockMvc.perform(
                get("/api/projects/{projectId}/admin", projectId)
                    .header(AUTH_HEADER, BEARER_TOKEN)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId.toString()))
            .andExpect(jsonPath("$.projectName").value("Admin View"))
            .andExpect(jsonPath("$.createdByUserId").value(actingUserId.toString()))
            .andExpect(jsonPath("$.archivedByUserId").value(nullValue()))
            .andExpect(jsonPath("$.archivedAt").value(nullValue()));

        verify(authzService).actingUserId(BEARER_TOKEN);
        verify(authzService).mustBeAdmin(actingUserId);
        verify(projectService).getById(projectId);
    }

    // -----------------------
    // PATCH /api/projects/{id}
    // -----------------------

    @Test
    void update_returns_200_and_updated_fields() throws Exception {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(authzService.actingUserId(BEARER_TOKEN)).thenReturn(actingUserId);

        Project updated = project(projectId, "Patched", "after", ProjectStatus.ARCHIVED, actingUserId);
        when(projectService.update(eq(projectId), any(UpdateProjectRequest.class), eq(actingUserId))).thenReturn(updated);

        UpdateProjectRequest req = new UpdateProjectRequest("Patched", "after", ProjectStatus.ARCHIVED);

        mockMvc.perform(
                patch("/api/projects/{projectId}", projectId)
                    .header(AUTH_HEADER, BEARER_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId.toString()))
            .andExpect(jsonPath("$.projectName").value("Patched"))
            .andExpect(jsonPath("$.projectDescription").value("after"))
            .andExpect(jsonPath("$.projectStatus").value("ARCHIVED"));

        verify(authzService).actingUserId(BEARER_TOKEN);
        verify(projectService).update(eq(projectId), any(UpdateProjectRequest.class), eq(actingUserId));
    }

    // -----------------------
    // DELETE /api/projects/{id}
    // -----------------------

    @Test
    void delete_returns_204_and_calls_archive() throws Exception {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(authzService.actingUserId(BEARER_TOKEN)).thenReturn(actingUserId);
        doNothing().when(projectService).archive(projectId, actingUserId);

        mockMvc.perform(
                delete("/api/projects/{projectId}", projectId)
                    .header(AUTH_HEADER, BEARER_TOKEN)
            )
            .andExpect(status().isNoContent());

        verify(authzService).actingUserId(BEARER_TOKEN);
        verify(projectService).archive(projectId, actingUserId);
    }
}