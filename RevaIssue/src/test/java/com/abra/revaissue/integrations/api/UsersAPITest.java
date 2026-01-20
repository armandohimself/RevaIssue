package com.abra.revaissue.integrations.api;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.abra.revaissue.dto.CreateUserDTO;
import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.enums.UserEnum.Role;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersAPITest {
    
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
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName("admin");
        credentials.setPassword("wrongpassword");

        given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post("/login")
        .then()
            .statusCode(401);
    }

    @Test
    public void loginNonexistentUserTest() {
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName("nonexistent");
        credentials.setPassword("password");

        given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post("/login")
        .then()
            .statusCode(401);
    }

    @Test
    public void loginEmptyUsernameTest() {
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName("");
        credentials.setPassword("password");

        given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post("/login")
        .then()
            .statusCode(401);
    }

    @Test
    public void loginEmptyPasswordTest() {
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName("admin");
        credentials.setPassword("");

        given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post("/login")
        .then()
            .statusCode(401);
    }

    @Test
    public void getCurrentUserSuccess() {
        // login
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

        // get the user
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/me")
        .then()
            .statusCode(200)
            .body("userName", equalTo("admin"))
            .body("userId", notNullValue());
    }

    @Test
    public void getCurrentUserInvalidTokenTest() {
        given()
            .header("Authorization", "invalid.token.here")
        .when()
            .get("/me")
        .then()
            .statusCode(401);
    }

    @Test
    public void getCurrentUserNoTokenTest() {
        given()
        .when()
            .get("/me")
        .then()
            .statusCode(400);
    }

    @Test
    public void getAllUsersSuccess() {
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName("admin");
        credentials.setPassword("password");

        String token = given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post("/login")
        .then()
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/all")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void getAllUsersNoTokenTest() {
        given()
        .when()
            .get("/all")
        .then()
            .statusCode(400);
    }

    @Test
    public void createUserSuccess() {
        // login
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setUserName("admin");
        credentials.setPassword("password");

        String token = given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post("/login")
        .then()
            .extract()
            .path("token");

        // add new user
        CreateUserDTO newUser = new CreateUserDTO();
        newUser.setUserName("newDeveloper" + System.nanoTime());
        newUser.setPassword("password");
        newUser.setRole(Role.DEVELOPER);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(newUser)
        .when()
            .post("/create")
        .then()
            .statusCode(200)
            .body("userName", equalTo(newUser.getUserName()));
    }

    @Test
    public void createUserNoTokenTest() {
        CreateUserDTO newUser = new CreateUserDTO();
        newUser.setUserName("newUser");
        newUser.setPassword("password");

        given()
            .contentType(ContentType.JSON)
            .body(newUser)
        .when()
            .post("/create")
        .then()
            .statusCode(400);
    }

    @Test
    public void getAllRoles() {
        given()
        .when()
            .get("/roles")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("", hasItems("ADMIN", "TESTER", "DEVELOPER"));
    }

}
