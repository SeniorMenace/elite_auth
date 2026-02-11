package org.example.eliteback.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Elite Auth API",
                version = "v1",
                description = "Authentication, onboarding and subscription API for Elite application."
        )
)
public class OpenApiConfig {

    @Value("${app.base-url:}")
    private String baseUrl;

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();
        if (StringUtils.hasText(baseUrl)) {
            String url = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
            openAPI.servers(List.of(
                    new Server().url(url).description("Production (HTTPS)"),
                    new Server().url("/").description("Current origin")
            ));
        } else {
            openAPI.servers(List.of(new Server().url("/").description("Current origin")));
        }
        return openAPI;
    }
}

