package com.abra.revaissue.integrations.api;

import static io.restassured.RestAssured.given;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.abra.revaissue.dto.CommentRequestDTO;
import com.abra.revaissue.dto.IssueCreateDTO;
import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.dto.project.CreateProjectRequest;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentControllerAPITest {

    @LocalServerPort
    int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User user;
    private User tester;
    private String adminToken;
    private String testerToken;
    private String projectId;
    private String issueId;

    @BeforeEach
    public void setup() {

        RestAssured.reset();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/api";

        // Prepare a user
        user = new User();
        user.setUserName("admin_" + UUID.randomUUID());
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        tester = new User();
        tester.setUserName("tester_" + UUID.randomUUID());
        tester.setPasswordHash(passwordEncoder.encode("password"));
        tester.setRole(Role.TESTER);
        userRepository.save(tester);

        // Login to get a token
        adminToken = login(user.getUserName(), "password");
        testerToken = login(tester.getUserName(), "password");

        // Prepare a project
        CreateProjectRequest projectRequest = new CreateProjectRequest(
                "Test Project", "Test Project description.");

        projectId = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(projectRequest)
                .when()
                .post("/projects")
                .then()
                .statusCode(200)
                .body("projectName", equalTo("Test Project"))
                .body("projectDescription", equalTo("Test Project description."))
                .extract()
                .path("projectId")
                .toString();

        // Prepare an issue
        IssueCreateDTO issueRequest = new IssueCreateDTO();
        issueRequest.setName("Test Issue");
        issueRequest.setDescription("Test Issue description.");
        issueRequest.setSeverity(IssueSeverity.LOW);
        issueRequest.setPriority(IssuePriority.LOW);

        issueId = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + testerToken)
                .body(issueRequest)
                .when()
                .post("/projects/" + projectId + "/issues")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test Issue"))
                .body("description", equalTo("Test Issue description."))
                .extract()
                .path("issueId")
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
                .extract()
                .path("token");
    }

    @Test
    public void createCommentPositiveTest() {
        CommentRequestDTO commentRequest = new CommentRequestDTO("This is a test comment",
                UUID.fromString(issueId));
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + testerToken)
                .body(commentRequest)
                .when()
                .post("/comments")
                .then()
                .statusCode(201)
                .body("message", equalTo("This is a test comment"))
                .body("commentId", notNullValue())
                .body("username", equalTo(tester.getUserName()))
                .body("time", notNullValue());
    }

    @Test
    public void createCommentUnauthorizedTest() {
        CommentRequestDTO commentRequest = new CommentRequestDTO("Unauthorized comment",
                UUID.fromString(issueId));
        given()
                .contentType(ContentType.JSON)
                .body(commentRequest)
                .when()
                .post("/comments")
                .then()
                .statusCode(401);
    }

    @Test
    public void createCommentInvalidTokenTest() {
        CommentRequestDTO commentRequest = new CommentRequestDTO("Invalid token comment",
                UUID.fromString(issueId));
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer invalidtoken")
                .body(commentRequest)
                .when()
                .post("/comments")
                .then()
                .statusCode(401);
    }

    @Test
    public void getCommentsByIssuePositiveTest() {
        // First, create a comment to ensure there is at least one comment for the issue
        CommentRequestDTO commentRequest = new CommentRequestDTO("This is a test comment",
                UUID.fromString(issueId));
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + testerToken)
                .body(commentRequest)
                .when()
                .post("/comments")
                .then()
                .statusCode(201);

        // Now, retrieve comments for the issue
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + testerToken)
                .when()
                .get("/comments/issue/" + issueId + "?page=0&size=10&sort=time,asc")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(1))
                .body("content[0].message", equalTo("This is a test comment"))
                .body("content[0].username", equalTo(tester.getUserName()));
    }

    @Test
    public void getCommentsByIssueUnauthorizedTest() {
        CommentRequestDTO commentRequest = new CommentRequestDTO("This is a test comment",
                UUID.fromString(issueId));
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + testerToken)
                .body(commentRequest)
                .when()
                .post("/comments")
                .then()
                .statusCode(201);
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/comments/issue/" + issueId + "?page=0&size=10&sort=time,asc")
                .then()
                .statusCode(401);
    }

    @Test
    public void getCommentsByIssueInvalidTokenTest() {
        CommentRequestDTO commentRequest = new CommentRequestDTO("This is a test comment",
                UUID.fromString(issueId));
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + testerToken)
                .body(commentRequest)
                .when()
                .post("/comments")
                .then()
                .statusCode(201);
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer invalidtoken")
                .when()
                .get("/comments/issue/" + issueId + "?page=0&size=10&sort=time,asc")
                .then()
                .statusCode(401);
    }
}
