package com.sbecomm.modernized.common;

import com.sbecomm.modernized.ModernizedEcommApplication;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ModernizedEcommApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    // Containers bypassed for local testing due to missing Docker
    static {
        System.out.println("Bypassing Testcontainers...");
    }

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/ecomm_db");
        registry.add("spring.datasource.username", () -> "admin");
        registry.add("spring.datasource.password", () -> "secure_postgres_pass_123");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> 6379);
        registry.add("spring.rabbitmq.host", () -> "localhost");
        registry.add("spring.rabbitmq.port", () -> 5672);
    }

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
    }

    protected io.restassured.specification.RequestSpecification request() {
        return RestAssured.given().port(port);
    }

    protected String getAdminToken() {
        mockJwt("admin-user", List.of("ADMIN"));
        return "mock-admin-token";
    }

    protected String getCustomerToken() {
        mockJwt("customer-user", List.of("USER"));
        return "mock-customer-token";
    }

    private void mockJwt(String subject, List<String> roles) {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", subject)
                .claim("realm_access", Map.of("roles", roles))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
    }
}
