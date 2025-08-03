package com.talentica.taskmanagement.config;








import org.springframework.stereotype.Service;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Springfox ApiInfoBuilder removed - use OpenAPI Info
// Springfox PathSelectors removed - use springdoc auto-configuration
// Springfox RequestHandlerSelectors removed - use springdoc auto-configuration
// Removed springfox import: springfox.documentation.service.*;
// Springfox DocumentationType removed - use OpenAPI
// Springfox SecurityContext removed - use OpenAPI security
import io.swagger.v3.oas.models.OpenAPI;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Management Service API")
                        .description("REST API for Task Management System with Workflow and Role-based Access Control")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Talentica Software")
                                .url("https://talentica.com")
                                .email("info@talentica.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT",
                                new SecurityScheme()
                                        .name("JWT")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .description("JWT token for API authentication")));
    }
}