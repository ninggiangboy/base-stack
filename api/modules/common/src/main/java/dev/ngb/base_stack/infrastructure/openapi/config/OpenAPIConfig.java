package dev.ngb.base_stack.infrastructure.openapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    
    private static final String BEARER_AUTH = "bearerAuth";
    private static final String API_KEY_AUTH = "apiKeyAuth";
    private static final String HEADER_API_KEY = "X-API-KEY";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Base Stack API").description("API documentation").version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH).addList(API_KEY_AUTH))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        BEARER_AUTH,
                                        new SecurityScheme()
                                                .name(BEARER_AUTH)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT"))
                                .addSecuritySchemes(
                                        API_KEY_AUTH,
                                        new SecurityScheme()
                                                .name(HEADER_API_KEY)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .description("API Key header")));
    }
}
