package com.abra.revaissue.integrations.api;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.abra.revaissue.dto.LoginRequestDTO;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogTransactionAPITest {
    
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @BeforeEach
    public void loginSetup() {
        RestAssured.basePath = "/api/logs";
    }

    private String getAdminToken() {
        RestAssured.basePath = "/api/users";
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName("admin");
        credentials.setPassword("password");

        String token = given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .extract()
            .path("token");

        RestAssured.basePath = "/api/logs";
        return "Bearer " + token;
    }

    @Test
    public void getAllLogsAsAdmin() {
        String authHeader = getAdminToken();

        given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .when()
            .get("/get-all")
        .then()
            .statusCode(200)
            .body("content", notNullValue());
    }

    @Test
    public void getAllLogsNoToken() {
        given()
        .when()
            .get("/get-all")
        .then()
            .statusCode(400);
    }

    @Test
    public void getIssueHistory() {
        String authHeader = getAdminToken();

        given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .when()
            .get("/issue/{issueId}", "123e4567-e89b-12d3-a456-426614174000")
        .then()
            .statusCode(200)
            .body("content", notNullValue());
    }

    @Test
    public void getIssueHistoryNoToken() {
        given()
            .queryParam("page", 0)
            .queryParam("size", 20)
        .when()
            .get("/issue/{issueId}", "123e4567-e89b-12d3-a456-426614174000")
        .then()
            .statusCode(400);
    }
}
