package com.sbecomm.modernized.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Modernized E-Commerce API", version = "1.0", description = "API Documentation for the E-Commerce platform"),
        security = {
            @SecurityRequirement(name = "bearerAuth"),
            @SecurityRequirement(name = "oauth2")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityScheme(
        name = "oauth2",
        type = SecuritySchemeType.OAUTH2,
        flows = @io.swagger.v3.oas.annotations.security.OAuthFlows(
            authorizationCode = @io.swagger.v3.oas.annotations.security.OAuthFlow(
                authorizationUrl = "${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/auth",
                tokenUrl = "${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/token",
                scopes = {
                    @io.swagger.v3.oas.annotations.security.OAuthScope(name = "openid", description = "OpenID Connect scope"),
                    @io.swagger.v3.oas.annotations.security.OAuthScope(name = "profile", description = "Profile scope")
                }
            )
        )
)
public class OpenApiConfig {
}
