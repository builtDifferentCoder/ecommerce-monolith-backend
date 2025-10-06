package com.monthly.ecommercemonolith.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi() {
        SecurityScheme bearerScheme =
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme(
                        "bearer").bearerFormat("JWT").description("JWT bearer" +
                        " token");
        SecurityRequirement bearerRequirement =
                new SecurityRequirement().addList("Bearer Authentication");

        return new OpenAPI().info(
                        new Info().title("Ecommerce Monolith API").version("1.0")
                                .description("This is a spring boot project")
                                .license(new License().name("Apache 2.0").url("https://hamza.com"))
                                .contact(new Contact().name("Hamza").email("hamza" +
                                        ".coder.794@gmail.com").url("https://github" +
                                        ".com/builtDifferentCoder"))
                ).externalDocs(new ExternalDocumentation().description("Project " +
                        "documentation"))
                .components(new Components().addSecuritySchemes(
                        "Bearer Authentication", bearerScheme)).addSecurityItem(bearerRequirement);
    }
}
