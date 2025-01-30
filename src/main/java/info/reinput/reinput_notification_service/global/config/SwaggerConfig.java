package info.reinput.reinput_notification_service.global.config;    

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.getServers().forEach(server -> {
                        server.addExtension("x-explorer-urls", Arrays.asList(
                            "/member/v3/api-docs",
                            "/insight/v3/api-docs",
                            "/reminder/v3/api-docs",
                            "/folder/v3/api-docs"
                        ));
                    });
                })
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(createServers())
                .info(createApiInfo())
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() != null) {
                operation.getParameters().removeIf(param ->
                        "X-User-Id".equalsIgnoreCase(param.getName()));
            }
            return operation;
        };
    }

    private List<Server> createServers() {
        return List.of(
                new Server().url("").description("Direct"),
                new Server().url("/reminder").description("Gateway")
        );
    }

    private Info createApiInfo() {
        return new Info()
                .title("Reinput Notification Service")
                .version("v0.0.1")
                .description("""
                        Reinput Notification Service (reminder service)
                        """);
    }
}