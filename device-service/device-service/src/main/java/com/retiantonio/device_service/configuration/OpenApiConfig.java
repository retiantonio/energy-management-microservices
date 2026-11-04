package com.retiantonio.device_service.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("User Service API").version("1.0")
                        .description("Dev: click Authorize and fill X-User-Id + X-User-Role "
                                + "(to simulate what Traefik ForwardAuth injects), or X-Internal-Key."))
                .components(new Components()
                        .addSecuritySchemes("userId", header("X-User-Id"))
                        .addSecuritySchemes("userRole", header("X-User-Role"))
                        .addSecuritySchemes("internalKey", header("X-Internal-Key")))
                .addSecurityItem(new SecurityRequirement().addList("userId").addList("userRole"))
                .addSecurityItem(new SecurityRequirement().addList("internalKey"));
    }

    private SecurityScheme header(String name) {
        return new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER).name(name);
    }
}