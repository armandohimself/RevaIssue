package com.abra.revaissue.E2E.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AdminLoginSteps {
    
    private String baseUrl;
    private Response lastResponse;
    private String token;
    
    @Given("the API base url is configured")
    public void the_api_base_url_is_configured() {
        // Start writing code to make these tests pass, here is my implementation below

        // 1) Prefer DatabaseURL
        String fromProp = System.getProperty("baseUrl");

        // 2) Then environment variable
        String fromEnv = System.getenv("REVAISSUE_BASE_URL");

        // 3) Default API Base URL
        baseUrl = (fromProp != null && !fromProp.isBlank())
            ? fromProp
            : (fromEnv != null && !fromEnv.isBlank())
                ? fromEnv
                : "http://localhost:8081";

        RestAssured.baseURI = baseUrl;
    }
    
    @When("the admin logs in with username {string} and password {string}")
    public void the_admin_logs_in_with_username_and_password(String userName, String password) {
        lastResponse = 
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"userName\":\"" + userName + "\",\"password\":\"" + password + "\"}")
            .when() 
                .post("api/users/login")
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

        // If your JSON uses "userName" (likely) this is correct.

        assertEquals(expectedUserName, actualUserName, () -> 
            "Expected userName=" + expectedUserName + " but got " + actualUserName 
            + "\nBody: " + lastResponse.asString()
        );
    }
}