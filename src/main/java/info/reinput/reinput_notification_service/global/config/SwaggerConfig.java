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

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> {
                    String customJs = """
                        window.onload = function() {
                            // 스타일 추가
                            const style = document.createElement('style');
                            style.textContent = `
                                .service-nav {
                                    padding: 10px 20px;
                                    background: #f8f9fa;
                                    margin-bottom: 20px;
                                    display: flex;
                                    align-items: center;
                                }
                                .dropdown {
                                    position: relative;
                                    display: inline-block;
                                }
                                .dropbtn {
                                    background-color: #4a6bff;
                                    color: white;
                                    padding: 10px 20px;
                                    border: none;
                                    border-radius: 4px;
                                    cursor: pointer;
                                    font-size: 14px;
                                }
                                .dropdown-content {
                                    display: none;
                                    position: absolute;
                                    background-color: #f9f9f9;
                                    min-width: 200px;
                                    box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
                                    z-index: 1000;
                                    border-radius: 4px;
                                }
                                .dropdown-content a {
                                    color: black;
                                    padding: 12px 16px;
                                    text-decoration: none;
                                    display: block;
                                }
                                .dropdown-content a:hover {
                                    background-color: #f1f1f1;
                                }
                                .dropdown:hover .dropdown-content {
                                    display: block;
                                }
                                .dropdown:hover .dropbtn {
                                    background-color: #3d5af1;
                                }
                                .current-service {
                                    background-color: #e6e9ff !important;
                                    font-weight: bold;
                                }
                            `;
                            document.head.appendChild(style);
                            
                            // 네비게이션 바 생성
                            const nav = document.createElement('div');
                            nav.className = 'service-nav';
                            
                            // 현재 URL에서 서비스 경로 추출
                            const currentPath = window.location.pathname;
                            const currentService = currentPath.split('/')[1]; // member, insight 등
                            
                            // 드롭다운 생성
                            const dropdown = document.createElement('div');
                            dropdown.className = 'dropdown';
                            
                            const dropbtn = document.createElement('button');
                            dropbtn.className = 'dropbtn';
                            dropbtn.innerText = 'Service APIs ▼';
                            
                            const dropdownContent = document.createElement('div');
                            dropdownContent.className = 'dropdown-content';
                            
                            const services = {
                                'Auth Service': {
                                    path: '/member/swagger-ui/index.html',
                                    id: 'member'
                                },
                                'Content Service': {
                                    path: '/insight/swagger-ui/index.html',
                                    id: 'insight'
                                },
                                'Notification Service': {
                                    path: '/reminder/swagger-ui/index.html',
                                    id: 'reminder'
                                },
                                'Workspace Service': {
                                    path: '/folder/swagger-ui/index.html',
                                    id: 'folder'
                                }
                            };
                            
                            Object.entries(services).forEach(([name, service]) => {
                                const link = document.createElement('a');
                                link.href = service.path;
                                link.innerText = name;
                                
                                // 현재 서비스 하이라이트
                                if (currentPath.includes(service.id)) {
                                    link.className = 'current-service';
                                }
                                
                                dropdownContent.appendChild(link);
                            });
                            
                            dropdown.appendChild(dropbtn);
                            dropdown.appendChild(dropdownContent);
                            nav.appendChild(dropdown);
                            
                            // Swagger UI에 네비게이션 바 추가
                            const header = document.querySelector('.swagger-ui');
                            header.insertBefore(nav, header.firstChild);
                        };
                        """;
                    
                    openApi.addExtension("x-customJs", customJs);
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
                new Server().url("/notification").description("Gateway")
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