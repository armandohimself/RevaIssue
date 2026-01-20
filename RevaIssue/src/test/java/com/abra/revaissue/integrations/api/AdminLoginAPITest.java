package com.abra.revaissue.integrations.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

class AdminLoginAPITest {

    private String baseUrl() {
        String fromProp = System.getProperty("baseUrl");
        String fromEnv = System.getenv("REVAISSUE_BASE_URL");

        if (fromProp != null && !fromProp.isBlank()) return fromProp;
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;

        return "http://localhost:8081";
    }

    @Test
    void login_returns_token_and_token_allows_me() {
        RestAssured.baseURI = baseUrl();

        // 1) Login
        Response loginRes =
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"userName\":\"admin\",\"password\":\"password\"}")
            .when()
                .post("/api/users/login")
            .then()
                .extract().response();

        assertEquals(200, loginRes.statusCode(), "Login failed. Body: " + loginRes.asString());

        String token = loginRes.jsonPath().getString("token");
        assertNotNull(token, "Token was null. Body: " + loginRes.asString());
        assertFalse(token.isBlank(), "Token was blank. Body: " + loginRes.asString());

        // 2) Call /me with token
        // If your AuthzService expects raw token, change header to token instead of "Bearer ..."
        Response meRes =
            RestAssured.given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/api/users/me")
            .then()
                .extract().response();

        assertEquals(200, meRes.statusCode(), "GET /me failed. Body: " + meRes.asString());
        assertEquals("admin", meRes.jsonPath().getString("userName"), "Unexpected user. Body: " + meRes.asString());
    }
}
