package com.abra.revaissue.integrations.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.notNullValue;

import com.abra.revaissue.dto.LoginRequestDTO;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public final class AuthHelpers {
    private AuthHelpers() {}

    // Helper method: logs in and returns the JWT token string.
    public static String loginAndGetAdminToken(String userName, String password) {
        LoginRequestDTO credentials = new LoginRequestDTO("admin", "password");

        Response loginResponse =
            given()
                .contentType(ContentType.JSON)
                .body(credentials)
            .when()
                .post("/users/login")
            .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("token", not(emptyString()))
                .extract().response();

        return loginResponse.jsonPath().getString("token");
    }
}
