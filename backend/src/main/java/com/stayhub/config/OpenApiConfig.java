package com.stayhub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "StayHub API",
                description = "REST API for the StayHub booking platform: property management, bookings, reviews, and authentication.",
                version = "v1"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Provide a JWT access token obtained from /api/v1/auth/login or /api/v1/auth/refresh"
)
public class OpenApiConfig {
}
