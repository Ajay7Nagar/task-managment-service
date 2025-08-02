package com.talentica.taskmanagement.config;



import org.springframework.stereotype.Service;
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
                        .title("API Documentation")
                        .description("API documentation generated with springdoc-openapi")
                        .version("1.0.0"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Task Management Service API")
                .description("REST API for Task Management System with Workflow and Role-based Access Control")
                .version("1.0.0")
                .contact(new Contact("Talentica Software", "https://talentica.com", "info@talentica.com"))
                .license("MIT License")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
}