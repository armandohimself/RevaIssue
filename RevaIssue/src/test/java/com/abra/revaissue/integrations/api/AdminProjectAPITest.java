package com.abra.revaissue.integrations.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.dto.project.CreateProjectRequest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/* Make sure to run ./gradlew bootRun when running ./gradlew test --tests "*AdminProjectAPITest*" */
class AdminProjectAPITest {

    // store admin token in between tests so we don't repeatedly login
    private static String adminToken;

    @BeforeAll
    static void setupServerAddress() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @BeforeEach
    void setupBasePath() {
        // every request begins with "/api"
        RestAssured.basePath = "/api";

        // Refresh token per each test since it expires every 15 mins
        if (adminToken == null) {
            adminToken = AuthHelpers.loginAndGetAdminToken("admin", "password");
        }
    }

    private static String authHeader() {
        // MUST include the space in Bearer + token
        return "Bearer " + adminToken;
    }

    private static Response createProject(String projectName, String projectDescription) {
        CreateProjectRequest payload = new CreateProjectRequest(
            projectName,
            projectDescription
        );

        return
        given()
            .header("Authorization", authHeader())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/projects")
        .then()
            .extract().response();
        }

    //! CREATE

    @Test
    void create_project_returns_200_and_project_response_field() {

        Response res = createProject("Test Admin Project API Test", "We are testing the API");

        res.then()
            .statusCode(200)
            .body("projectName", equalTo("Test Admin Project API Test"))
            .body("createdByUserId", notNullValue())
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue());
    }

    @Test
    void create_project_missing_header_returns_400() {
        // 400 means request was malformed

        given()
            .contentType(ContentType.JSON)
            .body(new CreateProjectRequest("Project is missing header", "Meaning no auth was sent, should return 400"))
        .when()
            .post("/projects")
        .then()
            .statusCode(400);
    }

    @Test
    void create_project_invalid_header_returns_401() {
        // 401 means rejected due to missing or invalid authentication credentials

        //authzService requires "Bearer" prefix
        given()
            .header("Authorization", "Bearrrer" + adminToken)
            .contentType(ContentType.JSON)
            .body(new CreateProjectRequest("Project with invalid header", "Should return 401"))
        .when()
            .post("/projects")
        .then()
            .statusCode(401);
    }

    //! LIST

    @Test
    void list_projects_returns_200_and_contains_project_name() {

        // Arrange: ensure at least one known project exists
        createProject("First Project", "Used for list test");

        given()
            .header("Authorization", authHeader())
        .when()
            .get("projects")
        .then()
            .statusCode(200)
            // response is a JSON array of ProjectResponse objects
            .body("size()", greaterThan(0))
            .body("projectName", hasItem("First Project"));

            //! Get by id

    }


}
