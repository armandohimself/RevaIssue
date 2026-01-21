package com.abra.revaissue.integrations.api;

import com.abra.revaissue.dto.IssueCreateDTO;
import com.abra.revaissue.dto.IssueUpdateDTO;
import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.dto.TokenTransport;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.repository.IssueRepository;
import com.abra.revaissue.repository.ProjectRepository;
import com.abra.revaissue.repository.UserRepository;
import com.abra.revaissue.service.JwtService;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IssueControllerAPITest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private JwtService jwtService;

    private String userToken;
    private UUID apiProjectId;

    @BeforeAll
    public static void urlSetup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @BeforeEach
    public void setup(){
        RestAssured.basePath = "/api";
        User tester = userRepository.findByUserName("apitester");
        String token = jwtService.createToken(tester.getUserId(), tester.getUserName(), tester.getRole());
        userToken = "Bearer " + token;
        List<Project> projects = projectRepository.findAll();
        Project apiProject = projects.stream().filter(project -> project.getProjectName()
                .equals("API Test Project"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("API Test Project not found. Did DataLoader run?"));
        apiProjectId = apiProject.getProjectId();
    }

    private UUID createIssueAndReturnId(String name) {
        IssueCreateDTO dto = new IssueCreateDTO();
        dto.setName(name);
        dto.setDescription("Created by API test");
        dto.setSeverity(IssueSeverity.LOW);
        dto.setPriority(IssuePriority.LOW);

        String issueIdString =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", userToken)
                        .body(dto)
                .when()
                        .post("/projects/" + apiProjectId + "/issues")
                .then()
                        .statusCode(201)
                        .body("issueId", notNullValue())
                        .body("name", equalTo(name))
                        .extract()
                        .path("issueId");

        return UUID.fromString(issueIdString);
    }

    @Test
    void createIssuePositiveTest(){
        IssueCreateDTO dto = new IssueCreateDTO();
        dto.setName("API Created Issue");
        dto.setDescription("Created via Rest Assured test");
        dto.setSeverity(IssueSeverity.LOW);
        dto.setPriority(IssuePriority.LOW);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .body(dto)
        .when()
                .post("/projects/" + apiProjectId + "/issues")
        .then()
                .statusCode(201)
                .body("issueId", notNullValue())
                .body("name", equalTo("API Created Issue"));
    }
    @Test
    void getIssuesForProjectPositiveTest() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/projects/" + apiProjectId + "/issues")
        .then()
                .statusCode(200)
                .body("$", notNullValue())
                .body("name", hasItems(
                        "API Issue Open Low",
                        "API Issue Open High",
                        "API Issue Closed Medium",
                        "API Issue Resolved High"
                ));
    }
    @Test
    void getIssuesForProject_filterStatusOpen() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("status", "OPEN")
        .when()
                .get("/projects/" + apiProjectId + "/issues")
        .then()
                .statusCode(200)
                .body("$", notNullValue())
                .body("name", not(hasItems("API Issue Closed Medium", "API Issue Resolved High")));
    }
    @Test
    void getIssuesForProject_filterSeverityHigh() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("severity", "HIGH")
        .when()
                .get("/projects/" + apiProjectId + "/issues")
        .then()
                .statusCode(200)
                .body("$", notNullValue())
                .body("name", not(hasItems("API Issue Closed Medium")));
    }
    @Test
    void getIssuesForProject_filterPriorityHigh() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("priority", "HIGH")
        .when()
                .get("/projects/" + apiProjectId + "/issues")
        .then()
                .statusCode(200)
                .body("$", notNullValue())
                .body("name", not(hasItems("API Issue Closed Medium", "API Issue Open Low")));
    }
    @Test
    void getIssueByIdPositiveTest() {
        UUID issueId = createIssueAndReturnId("API Created Issue For Get");

        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/issues/" + issueId)
        .then()
                .statusCode(200)
                .body("issueId", equalTo(issueId.toString()))
                .body("name", equalTo("API Created Issue For Get"));
    }
    @Test
    void updateIssuePositiveTest() {
        UUID issueId = createIssueAndReturnId("API Created Issue For Update");

        IssueUpdateDTO dto = new IssueUpdateDTO();
        dto.setDescription("Updated by API test");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .body(dto)
        .when()
                .put("/issues/" + issueId)
        .then()
                .statusCode(200)
                .body("issueId", equalTo(issueId.toString()));
    }
    @Test
    void updateIssueStatusPositiveTest() {
        UUID issueId = createIssueAndReturnId("API Created Issue For Status");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
                .queryParam("status", "CLOSED")
        .when()
                .put("/issues/" + issueId + "/status")
        .then()
                .statusCode(200)
                .body("issueId", equalTo(issueId.toString()))
                .body("status", equalTo("CLOSED"));
    }
    @Test
    void deleteIssuePositiveTest() {
        UUID issueId = createIssueAndReturnId("API Created Issue For Delete");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
        .when()
                .delete("/issues/" + issueId)
                .then()
                .statusCode(204);
    }
    @Test
    void assignDeveloperPositiveTest() {
        UUID issueId = createIssueAndReturnId("API Created Issue For Assign");

        User dev1 = userRepository.findByUserName("apidev1");
        UUID devId = dev1.getUserId();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", userToken)
        .when()
                .put("/issues/" + issueId + "/assign/" + devId)
        .then()
                .statusCode(200)
                .body("issueId", equalTo(issueId.toString()))
                .body("name", equalTo("API Created Issue For Assign"));
    }

    @Test
    void getIssuesAssignedToUserPositiveTest() {
        User dev1 = userRepository.findByUserName("apidev1");
        UUID dev1Id = dev1.getUserId();

        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/users/" + dev1Id + "/assigned-issues")
        .then()
                .statusCode(200)
                .body("$", notNullValue())
                .body("name", hasItems(
                        "API Issue Open Low",
                        "API Issue Closed Medium"
                ));
    }

    @Test
    void getIssuesCreatedByUserPositiveTest() {
        User tester = userRepository.findByUserName("apitester");
        UUID testerId = tester.getUserId();

        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/users/" + testerId + "/created-issues")
        .then()
                .statusCode(200)
                .body("$", notNullValue())
                .body("name", hasItems(
                        "API Issue Open Low",
                        "API Issue Open High"
                ));
    }
}
