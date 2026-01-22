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
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerAPITest {

    @LocalServerPort
    int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String adminAuthHeader; // "Bearer <jwt>"

    @BeforeEach
    void setup() {
        RestAssured.reset();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        // Create an admin user
        User admin = new User();
        
        admin.setUserName("admin_" + UUID.randomUUID());
        admin.setPasswordHash(passwordEncoder.encode("password"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Login to get token
        adminAuthHeader = "Bearer " + login(admin.getUserName(), "password");
    }

    private String login(String username, String password) {
        
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName(username);
        credentials.setPassword(password);

        return given()
                .contentType(ContentType.JSON)
                .body(credentials)
            .when()
                .post("/api/users/login")
            .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token");
    }

    private String createProjectAndReturnId(String name, String description) {
        String body = """
            {
              "projectName": "%s",
              "projectDescription": "%s"
            }
            """.formatted(name, description);

        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", adminAuthHeader)
                .body(body)
            .when()
                .post("/api/projects")
            .then()
                .statusCode(200)
                .body("projectId", notNullValue())
                .body("projectName", equalTo(name))
                .body("projectDescription", equalTo(description))
                .body("projectStatus", equalTo("ACTIVE"))
                .extract()
                .path("projectId")
                .toString();
    }

    @Test
    void create_list_get_patch_delete_archive_flow() {
        // CREATE
        String projectId = createProjectAndReturnId("API Project", "Created by ProjectControllerAPITest");

        // LIST
        given()
                .header("Authorization", adminAuthHeader)
            .when()
                .get("/api/projects")
            .then()
                .statusCode(200)
                .body("projectId", hasItem(projectId))
                .body("projectName", hasItem("API Project"));

        // GET BY ID
        given()
                .header("Authorization", adminAuthHeader)
            .when()
                .get("/api/projects/" + projectId)
            .then()
                .statusCode(200)
                .body("projectId", equalTo(projectId))
                .body("projectName", equalTo("API Project"))
                .body("projectStatus", equalTo("ACTIVE"));

        // ADMIN VIEW
        given()
                .header("Authorization", adminAuthHeader)
            .when()
                .get("/api/projects/" + projectId + "/admin")
            .then()
                .statusCode(200)
                .body("projectId", equalTo(projectId))
                .body("createdByUserId", notNullValue())
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .body("archivedByUserId", nullValue())
                .body("archivedAt", nullValue());

        // PATCH
        String patchBody = """
            {
              "projectName": "API Project Patched",
              "projectDescription": "Updated desc",
              "projectStatus": "ARCHIVED"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", adminAuthHeader)
                .body(patchBody)
            .when()
                .patch("/api/projects/" + projectId)
            .then()
                .statusCode(200)
                .body("projectId", equalTo(projectId))
                .body("projectName", equalTo("API Project Patched"))
                .body("projectDescription", equalTo("Updated desc"))
                .body("projectStatus", equalTo("ARCHIVED"));

        // DELETE (archive)
        given()
                .header("Authorization", adminAuthHeader)
            .when()
                .delete("/api/projects/" + projectId)
            .then()
                .statusCode(204);

        // Confirm still exists and status is ARCHIVED
        given()
                .header("Authorization", adminAuthHeader)
            .when()
                .get("/api/projects/" + projectId)
            .then()
                .statusCode(200)
                .body("projectId", equalTo(projectId))
                .body("projectStatus", equalTo("ARCHIVED"));
    }
}
