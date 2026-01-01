package com.aitrics.vital;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Vital Signs API",
        version = "1.0",
        description = "API for managing patient vital signs and risk assessment"
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local server")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class VitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(VitalApplication.class, args);
    }
}