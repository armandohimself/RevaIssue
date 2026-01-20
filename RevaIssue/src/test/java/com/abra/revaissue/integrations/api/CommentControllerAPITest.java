package com.abra.revaissue.integrations.api;

import static io.restassured.RestAssured.given;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CommentControllerAPITest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User user;
    private String token;
    private String projectId;
    private String issueId;

    // @Autowired
    // public CommentControllerAPITest(UserRepository userRepository,
    // BCryptPasswordEncoder passwordEncoder) {
    // this.userRepository = userRepository;
    // this.passwordEncoder = passwordEncoder;
    // }

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @BeforeEach
    public void init() {

        // Prepare a user
        user = new User();
        user.setUserName("admin_" + UUID.randomUUID());
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        // Login to get a token
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName(user.getUserName());
        credentials.setPassword("password");
        token = given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        // Prepare a project
        CreateProjectRequest projectRequest = new CreateProjectRequest(
                "Test Project", "Test Project description.");
        projectId = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(projectRequest)
                .when()
                .post("/api/projects")
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
                .header("Authorization", "Bearer " + token)
                .body(issueRequest)
                .when()
                .post("/api/projects/" + projectId + "/issues")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test Issue"))
                .body("description", equalTo("Test Issue description."))
                .extract()
                .path("issueId")
                .toString();

    }

    @Test
    public void createCommentPositiveTest() {
        CommentRequestDTO commentRequest = new CommentRequestDTO("This is a test comment",
                UUID.fromString(issueId));
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(commentRequest)
                .when()
                .post("/comments")
                .then()
                .statusCode(201)
                .body("message", equalTo("This is a test comment"))
                .body("commentId", notNullValue())
                .body("username", equalTo(user.getUserName()))
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
                .header("Authorization", "Bearer " + token)
                .body(commentRequest)
                .when()
                .post("/comments")
                .then()
                .statusCode(201);

        // Now, retrieve comments for the issue
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/comments/issue/" + issueId + "?page=0&size=10&sort=time,asc")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(1))
                .body("content[0].message", equalTo("This is a test comment"))
                .body("content[0].username", equalTo(user.getUserName()));
    }

    @Test
    public void getCommentsByIssueUnauthorizedTest() {
        CommentRequestDTO commentRequest = new CommentRequestDTO("This is a test comment",
                UUID.fromString(issueId));
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
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
                .header("Authorization", "Bearer " + token)
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
