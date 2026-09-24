# Plano de Implementação: Swagger/OpenAPI

## Objetivo
Adicionar documentação Swagger (OpenAPI 3) ao projeto Spring Boot 3.x com suporte a autenticação JWT.

---

## 1. Dependência Maven (`pom.xml`)

Adicionar dentro de `<dependencies>`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

---

## 2. Configuração Swagger (`SwaggerConfig.java`)

**Arquivo:** `src/main/java/estudos/security/project/security/config/SwaggerConfig.java`

```java
package estudos.security.project.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project Security API")
                        .version("1.0")
                        .description("API documentation for Project Security"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
```

---

## 3. Anotações nos Controllers (Opcional - Recomendado)

Adicionar anotações para documentar endpoints:

```java
// Exemplo no AuthController
@Tag(name = "Autenticação", description = "Endpoints de login e registro")
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Operation(summary = "Login", description = "Autentica usuário e retorna token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        // ...
    }
}
```

---

## 4. Acesso à Documentação

Após rodar a aplicação (`./mvnw spring-boot:run`):

| Interface | URL |
|-----------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |
| **OpenAPI YAML** | http://localhost:8080/v3/api-docs.yaml |

---

## 5. Testando Endpoints Protegidos no Swagger

1. Acesse `http://localhost:8080/swagger-ui.html`
2. Clique no botão **"Authorize"** (cadeado) no topo
3. Cole o token JWT no formato: `Bearer <seu-token>`
4. Clique em **Authorize**
5. Agora pode testar endpoints com `@PreAuthorize` ou `security` requerido

---

## Checklist de Verificação

- [ ] Dependência adicionada no `pom.xml`
- [ ] `mvn clean install` ou `./mvnw dependency:resolve` executado
- [ ] Classe `SwaggerConfig.java` criada
- [ ] Aplicação inicia sem erros
- [ ] Swagger UI acessível em `/swagger-ui.html`
- [ ] Autenticação Bearer funcionando no Swagger
- [ ] (Opcional) Anotações `@Tag`, `@Operation`, `@ApiResponse` nos controllers