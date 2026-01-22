package com.abra.revaissue.E2E.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.dto.project.CreateProjectRequest;
import com.abra.revaissue.dto.project.UpdateProjectRequest;
import com.abra.revaissue.enums.ProjectStatus;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class SharedAPISteps {

    private String baseUrl;
    private Response lastResponse;
    private String token;

    private String lastProjectId;

    //! ADMIN API STEPS

    @Given("the API base url is configured")
    public void the_api_base_url_is_configured() {
        // Start writing code to make these tests pass, here is my implementation below

        // 1) Prefer DatabaseURL
        String fromProp = System.getProperty("baseUrl");
        System.out.println("baseUrl prop=" + System.getProperty("baseUrl"));

        // 2) Then environment variable
        String fromEnv = System.getenv("REVAISSUE_BASE_URL");
        System.out.println("baseUrl env =" + System.getenv("REVAISSUE_BASE_URL"));

        // 3) Default API Base URL
        baseUrl = (fromProp != null && !fromProp.isBlank())
            ? fromProp
            : (fromEnv != null && !fromEnv.isBlank())
                ? fromEnv
                : "http://localhost:8081";

        // host+port lives here
        RestAssured.baseURI = baseUrl;

        // prefix for all controllers
        RestAssured.basePath = "/api";

        System.out.println("baseUrl chosen=" + RestAssured.baseURI + RestAssured.basePath);
    }

    @When("the admin logs in with username {string} and password {string}")
    public void the_admin_logs_in_with_username_and_password(String userName, String password) {
        LoginRequestDTO payload = new LoginRequestDTO(userName, password);

        lastResponse =
            RestAssured.given()
                .contentType(ContentType.JSON)
                // .body("{\"userName\":\"" + userName + "\",\"password\":\"" + password + "\"}")
                .body(payload)
            .when()
                .post("/users/login")
            .then()
                .extract().response();
    }
    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        assertNotNull(lastResponse, "No response captured yet. Did a When step run?");
        assertEquals(expectedStatus.intValue(), lastResponse.statusCode(), () ->
            "Expected HTTP " + expectedStatus + " but got " + lastResponse.statusCode()
            + "\nBody: " + lastResponse.asString()
        );
    }

    @Then("the response should contain a token")
    public void the_response_should_contain_a_token() {
        assertNotNull(lastResponse, "No response captured yet. Did login run?");
        token = lastResponse.jsonPath().getString("token");

        assertNotNull(token,  "token was null. Body: " + lastResponse.asString());
        assertFalse(token.isBlank(), "token was blank. Body: " + lastResponse.asString());
    }


    @When("the client calls {string} with that token")
    public void the_client_calls_with_that_token(String path) {
        assertNotNull(token, "No token captured. Did you run the 'token' Then step?");

        String authHeader = "Bearer " + token;

        lastResponse =
            RestAssured.given()
                .header("Authorization", authHeader) // RequestSpecification
            .when()
                .get(path)
            .then()
                .extract().response();
    }

    @Then("the current user name should be {string}")
    public void the_current_user_name_should_be_admin(String expectedUserName) {
        assertNotNull(lastResponse, "No response captured yet.");

        String actualUserName = lastResponse.jsonPath().getString("userName");

        assertEquals(expectedUserName, actualUserName, () ->
            "Expected userName=" + expectedUserName + " but got " + actualUserName
            + "\nBody: " + lastResponse.asString()
        );
    }

    //! PROJECTS API STEPS


    //! Admin Creates Project 

    @When("the admin creates a project named {string} with description {string}")
    public void the_admin_creates_a_project(String name, String description) {
        assertNotNull(token, "No token captured yet.");

        CreateProjectRequest payload = new CreateProjectRequest(name, description);

        lastResponse =
            RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(payload)
            .when()
                .post("/projects")
            .then()
                .extract().response();

        // remember created id for later steps if needed
        lastProjectId = lastResponse.jsonPath().getString("projectId");
    }

    @Then("the response should contain a project id")
    public void the_response_should_contain_a_project_id() {
        String projectId = lastResponse.jsonPath().getString("projectId");
        assertNotNull(projectId, "projectId was null. Body: " + lastResponse.asString());
        assertFalse(projectId.isBlank(), "projectId was blank. Body: " + lastResponse.asString());
    }

    //! Admin Gets Projects 

    @When("the admin lists projects")
    public void the_admin_lists_projects() {
        assertNotNull(token, "No token captured yet.");

        lastResponse =
            RestAssured.given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/projects")
            .then()
                .extract().response();
    }

    @Then("the response should contain a project named {string}")
    public void the_response_should_contain_a_project_named(String expectedName) {
        assertNotNull(lastResponse, "No response captured.");
        // Response is a JSON array; RestAssured jsonPath can pull list of names
        var names = lastResponse.jsonPath().getList("projectName", String.class);
        assertTrue(names.contains(expectedName),
            () -> "Expected list to contain projectName=" + expectedName
                + "\nActual names: " + names
                + "\nBody: " + lastResponse.asString()
        );
    }

    //! Admin Gets Project

    @When("the admin requests the admin view for that project")
    public void the_admin_requests_admin_view_for_that_project() {
        assertNotNull(token, "No token captured yet.");
        assertNotNull(lastProjectId, "No projectId stored yet. Did you create a project first?");

        lastResponse =
            RestAssured.given()
                .header("Authorization", "Bearer " + token)
            .when()
                // basePath is "/api", so this becomes /api/projects/{id}/admin
                .get("/projects/" + lastProjectId + "/admin")
            .then()
                .extract().response();
    }

    @Then("the admin project response should match the created project")
    public void the_admin_project_response_should_match_created_project() {
        assertNotNull(lastResponse, "No response captured.");
        assertNotNull(lastProjectId, "No projectId stored.");

        String projectId = lastResponse.jsonPath().getString("projectId");
        assertEquals(lastProjectId, projectId, () ->
            "Expected admin view projectId=" + lastProjectId
            + "\nBody: " + lastResponse.asString()
        );

        // These should exist regardless of your audit rules
        assertNotNull(lastResponse.jsonPath().getString("createdByUserId"), "createdByUserId missing/null");
        assertNotNull(lastResponse.jsonPath().getString("createdAt"), "createdAt missing/null");
        assertNotNull(lastResponse.jsonPath().getString("updatedAt"), "updatedAt missing/null");
    }

    @Then("the admin project archived fields should be empty")
    public void the_admin_project_archived_fields_should_be_empty() {
        assertNotNull(lastResponse, "No response captured.");

        assertNull(lastResponse.jsonPath().getString("archivedByUserId"), "archivedByUserId should be null");
        assertNull(lastResponse.jsonPath().getString("archivedAt"), "archivedAt should be null");
    }

    @Then("the admin project archived fields should be set")
    public void the_admin_project_archived_fields_should_be_set() {
        assertNotNull(lastResponse, "No response captured.");

        assertNotNull(lastResponse.jsonPath().getString("archivedByUserId"), "archivedByUserId should be set");
        assertNotNull(lastResponse.jsonPath().getString("archivedAt"), "archivedAt should be set");
        assertNotNull(lastResponse.jsonPath().getString("statusUpdatedByUserId"), "statusUpdatedByUserId should be set");
    }

    //! Admin Patch Project

    @When("the admin updates that project to name {string} description {string} status {string}")
    public void the_admin_updates_that_project(String name, String description, String status) {

        assertNotNull(token, "No token captured yet.");
        assertNotNull(lastProjectId, "No projectId stored yet.");

        ProjectStatus parsedStatus = ProjectStatus.valueOf(status); // must match enum exactly

        UpdateProjectRequest payload = new UpdateProjectRequest(
            name,
            description,
            parsedStatus
        );

        lastResponse =
            RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(payload)
            .when()
                .patch("/projects/" + lastProjectId)
            .then()
                .extract().response();
    }

    @When("the admin fetches that project by id")
    public void the_admin_fetches_that_project_by_id() {
    assertNotNull(token, "No token captured yet.");
    assertNotNull(lastProjectId, "No projectId stored yet.");

    lastResponse =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/projects/" + lastProjectId)
        .then()
            .extract().response();
}

    @Then("the project name should be {string}")
    public void the_project_name_should_be(String expected) {
        assertNotNull(lastResponse, "No response captured.");
        assertEquals(expected, lastResponse.jsonPath().getString("projectName"),
            () -> "Body: " + lastResponse.asString());
    }

    @Then("the project description should be {string}")
    public void the_project_description_should_be(String expected) {
        assertNotNull(lastResponse, "No response captured.");
        assertEquals(expected, lastResponse.jsonPath().getString("projectDescription"),
            () -> "Body: " + lastResponse.asString());
    }

    @Then("the project status should be {string}")
    public void the_project_status_should_be(String expected) {
        assertNotNull(lastResponse, "No response captured.");
        assertEquals(expected, lastResponse.jsonPath().getString("projectStatus"),
            () -> "Body: " + lastResponse.asString());
    }

    //! Admin Deletes Project

    @When("the admin archives that project")
    public void the_admin_archives_that_project() {
        assertNotNull(token, "No token captured yet.");
        assertNotNull(lastProjectId, "No projectId stored yet.");

        lastResponse =
            RestAssured.given()
                .header("Authorization", "Bearer " + token)
            .when()
                .delete("/projects/" + lastProjectId)
            .then()
                .extract().response();
    }
}