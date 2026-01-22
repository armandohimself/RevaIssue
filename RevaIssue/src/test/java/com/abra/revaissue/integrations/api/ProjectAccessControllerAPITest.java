package com.abra.revaissue.integrations.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.dto.project.CreateProjectRequest;
import com.abra.revaissue.dto.project.GrantProjectAccessRequest;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.ProjectRole;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectAccessControllerAPITest {

    @LocalServerPort
    int port;

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    private User admin;
    private User tester;

    private String adminToken;
    private String testerToken;
    private String projectId;

    @BeforeEach
    void setup() {
        RestAssured.reset();
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api";
        RestAssured.port = port;

        // Create fresh users (avoid collisions)
        admin = new User();
        admin.setUserName("admin_pa_" + UUID.randomUUID());
        admin.setPasswordHash(passwordEncoder.encode("password"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        tester = new User();
        tester.setUserName("tester_project_access" + UUID.randomUUID());
        tester.setPasswordHash(passwordEncoder.encode("password"));
        tester.setRole(Role.TESTER);
        userRepository.save(tester);

        adminToken = login(admin.getUserName(), "password");
        testerToken = login(tester.getUserName(), "password");

        // Create a project as admin
        CreateProjectRequest projectReq = new CreateProjectRequest(
            "Project Access Test Project",
            "Created by ProjectAccessControllerAPITest"
        );

        projectId =
            given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(projectReq)
            .when()
                .post("/projects")
            .then()
                .statusCode(200)
                .body("projectId", notNullValue())
                .extract()
                .path("projectId")
                .toString();
    }

    private String login(String username, String password) {
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName(username);
        credentials.setPassword(password);

        return given()
                .contentType(ContentType.JSON)
                .body(credentials)
            .when()
                .post("/users/login")
            .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token");
    }

    private String grantTester(ProjectRole role) {
        GrantProjectAccessRequest req = new GrantProjectAccessRequest(tester.getUserId(), role);

        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(req)
            .when()
                .post("/projects/{projectId}/access", projectId)
            .then()
                .statusCode(200)
                .body("projectAccessId", notNullValue())
                .body("projectId", equalTo(projectId))
                .body("userId", equalTo(tester.getUserId().toString()))
                .body("projectRole", equalTo(role.name()))
                .extract()
                .path("projectAccessId")
                .toString();
    }

    @Test
    void grant_access_admin_can_grant_returns_200_and_access_response() {
        grantTester(ProjectRole.TESTER);
    }

    @Test
    void list_access_admin_can_list_active_access_rows() {
        grantTester(ProjectRole.TESTER);

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/projects/{projectId}/access", projectId)
        .then()
            .statusCode(200)
            .body("$", notNullValue())
            .body("size()", greaterThanOrEqualTo(1))
            .body("userId", hasItem(tester.getUserId().toString()))
            .body("projectRole", hasItem("TESTER"));
    }

    @Test
    void access_all_admin_can_list_members_as_users() {
        grantTester(ProjectRole.TESTER);

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/projects/{projectId}/access/all", projectId)
        .then()
            .statusCode(200)
            .body("$", notNullValue())
            .body("userName", hasItem(tester.getUserName()))
            .body("userId", hasItem(tester.getUserId().toString()));
    }

    @Test
    void access_all_member_can_list_members_as_users() {
        // Make tester a member first
        grantTester(ProjectRole.TESTER);

        given()
            .header("Authorization", "Bearer " + testerToken)
        .when()
            .get("/projects/{projectId}/access/all", projectId)
        .then()
            .statusCode(200)
            .body("$", notNullValue())
            .body("userName", hasItem(tester.getUserName()))
            .body("userId", hasItem(tester.getUserId().toString()));
    }

    @Test
    void revoke_access_admin_can_revoke_and_member_disappears_from_active_access_list() {
        grantTester(ProjectRole.TESTER);

        // revoke
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .delete("/projects/{projectId}/access/{userId}", projectId, tester.getUserId())
        .then()
            .statusCode(204);

        // confirm removed from active access rows
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/projects/{projectId}/access", projectId)
        .then()
            .statusCode(200)
            .body("userId", not(hasItem(tester.getUserId().toString())));
    }
}
