package com.abra.revaissue.integrations.api;

import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.abra.revaissue.dto.LoginRequestDTO;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AdminLoginAPITest {
    
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @BeforeEach
    public void loginSetup() {
        RestAssured.basePath = "/api/users";
    }

    @Test
    public void adminLoginPositiveTest() {
        LoginRequestDTO credentials = new LoginRequestDTO();

        credentials.setUserName("admin");
        credentials.setPassword("password");

        given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue());
    }

    @Test
    public void adminLoginNegativeTest() {

    }
}
