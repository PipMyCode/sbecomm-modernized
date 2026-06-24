package com.sbecomm.modernized.catalog.presentation.rest;

import com.sbecomm.modernized.common.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

class CatalogControllerIT extends BaseIntegrationTest {

    @Test
    void shouldSecurelyCreateCategoryAndProductAndFetchThem() {
        // 1. Get Token
        String adminToken = getAdminToken();

        // 2. Create Category
        String categoryId = request()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + adminToken)
            .body("""
                {
                    "name": "Electronics",
                    "description": "Gadgets and devices"
                }
            """)
        .when()
            .post("/api/v1/catalog/categories")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("Electronics"))
            .extract().path("id");

        // 3. Create Product
        String productJson = String.format("""
                {
                    "name": "Smartphone",
                    "description": "Latest model smartphone",
                    "price": 999.99,
                    "stockQuantity": 100,
                    "categoryId": "%s"
                }
            """, categoryId);

        request()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + adminToken)
            .body(productJson)
        .when()
            .post("/api/v1/catalog/products")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("Smartphone"));

        // 4. Fetch Products Publicly using Java HttpClient to bypass RestAssured Groovy bugs
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + port + "/api/v1/catalog/products"))
                .GET()
                .header("Accept", "application/json")
                .build();
                
            java.net.http.HttpResponse<String> httpResponse = client.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
            
            org.assertj.core.api.Assertions.assertThat(httpResponse.statusCode()).isEqualTo(200);
            org.assertj.core.api.Assertions.assertThat(httpResponse.body()).contains("Smartphone");
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch products", e);
        }
    }

    @Test
    void shouldDenyProductCreationToNonAdmins() {
        String customerToken = getCustomerToken();

        request()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + customerToken)
            .body("""
                {
                    "name": "Hacked Product",
                    "description": "Should not be created",
                    "price": 1.00,
                    "stockQuantity": 1,
                    "categoryId": "some-category"
                }
            """)
        .when()
            .post("/api/v1/catalog/products")
        .then()
            .statusCode(403);
    }
}
