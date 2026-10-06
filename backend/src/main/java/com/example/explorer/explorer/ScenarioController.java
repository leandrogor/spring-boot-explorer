package com.example.explorer.explorer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/explorer/scenarios")
public class ScenarioController {

    public record RequestExample(String method, String url, Map<String, String> headers, String body, String description) {}
    public record ScenarioStep(int stepNumber, String description, RequestExample request) {}
    public record BridgeInfo(String express, String aspnet) {}
    public record SourceRef(String label, String file, Integer startLine, Integer endLine) {}
    public record ScenarioInfo(String id, String title, String story, List<String> concepts, List<ScenarioStep> steps, BridgeInfo bridge, List<String> pitfalls, List<String> interviewQuestions, List<SourceRef> sourceRefs) {}

    private final List<ScenarioInfo> scenarios;

    public ScenarioController() {
        scenarios = List.of(
            new ScenarioInfo("list-products", "List and filter products", "Basic GET endpoint with query params",
                List.of("@RestController", "@GetMapping", "@RequestParam", "Records", "Streams", "Pagination"),
                List.of(
                    new ScenarioStep(1, "Get all products", new RequestExample("GET", "/api/products", Map.of(), "", "Fetches page 0 size 10")),
                    new ScenarioStep(2, "Filter by name", new RequestExample("GET", "/api/products?name=laptop", Map.of(), "", "Filters products containing laptop")),
                    new ScenarioStep(3, "Filter by category and paginate", new RequestExample("GET", "/api/products?categoryId=1&page=0&size=2", Map.of(), "", "Category 1, max 2 items"))
                ),
                new BridgeInfo("Express router.get()", "ASP.NET [HttpGet]"),
                List.of("N+1 queries if using JPA incorrectly"),
                List.of("What is the difference between @RequestParam and @PathVariable?"),
                List.of(
                    new SourceRef("Product Controller", "com/example/explorer/web/controller/ProductController.java", null, null),
                    new SourceRef("Product Service", "com/example/explorer/domain/service/ProductService.java", null, null)
                )
            ),
            new ScenarioInfo("invalid-product", "Create product with invalid data", "Validating request bodies",
                List.of("Bean Validation", "@Valid", "@ControllerAdvice", "ProblemDetail"),
                List.of(
                    new ScenarioStep(1, "Empty body", new RequestExample("POST", "/api/products", Map.of("Authorization", "Bearer {{admin_token}}"), "{}", "Missing required fields")),
                    new ScenarioStep(2, "Negative price", new RequestExample("POST", "/api/products", Map.of("Authorization", "Bearer {{admin_token}}"), "{\"sku\":\"PROD-1\",\"name\":\"Test\",\"price\":-10,\"stock\":5,\"categoryId\":1}", "Price cannot be negative")),
                    new ScenarioStep(3, "Duplicate SKU", new RequestExample("POST", "/api/products", Map.of("Authorization", "Bearer {{admin_token}}"), "{\"sku\":\"LAP-001\",\"name\":\"Test\",\"price\":10,\"stock\":5,\"categoryId\":1}", "SKU already exists"))
                ),
                new BridgeInfo("class-validator/Zod", "DataAnnotations"),
                List.of("Forgetting @Valid on the controller parameter"),
                List.of("How do you handle global validation exceptions in Spring?"),
                List.of(
                    new SourceRef("Global Exception Handler", "com/example/explorer/web/error/GlobalExceptionHandler.java", null, null)
                )
            ),
            new ScenarioInfo("confirm-order-ok", "Confirm order with sufficient stock", "Happy path domain logic",
                List.of("DI", "@Service", "BigDecimal", "ApplicationEventPublisher", "@Async"),
                List.of(
                    new ScenarioStep(1, "Create draft", new RequestExample("POST", "/api/orders", Map.of("Authorization", "Bearer {{user1_token}}"), "{\"lines\":[{\"productId\":1,\"quantity\":1}]}", "Creates order")),
                    new ScenarioStep(2, "Confirm order", new RequestExample("PATCH", "/api/orders/1/confirm", Map.of("Authorization", "Bearer {{user1_token}}"), "", "Confirms order and reserves stock"))
                ),
                new BridgeInfo("manual DI", "ASP.NET DI container"),
                List.of("Floating point inaccuracies with double instead of BigDecimal"),
                List.of("Why use BigDecimal for currency?"),
                List.of(
                    new SourceRef("Order Service", "com/example/explorer/domain/service/OrderService.java", null, null)
                )
            ),
            new ScenarioInfo("confirm-order-no-stock", "Confirm order without stock", "Business logic exceptions",
                List.of("Business exceptions", "@ControllerAdvice", "409 Conflict", "ProblemDetail extensions"),
                List.of(
                    new ScenarioStep(1, "Create order with large qty", new RequestExample("POST", "/api/orders", Map.of("Authorization", "Bearer {{user1_token}}"), "{\"lines\":[{\"productId\":1,\"quantity\":999}]}", "Creates draft")),
                    new ScenarioStep(2, "Confirm fails", new RequestExample("PATCH", "/api/orders/2/confirm", Map.of("Authorization", "Bearer {{user1_token}}"), "", "Fails with 409 Conflict"))
                ),
                new BridgeInfo("custom Error classes", "custom exceptions"),
                List.of("Using generic RuntimeException instead of custom domain exceptions"),
                List.of("What is ProblemDetail and RFC 7807?"),
                List.of(
                    new SourceRef("Insufficient Stock Exception", "com/example/explorer/domain/exception/InsufficientStockException.java", null, null)
                )
            ),
            new ScenarioInfo("security-access", "Security and access control", "Authentication and authorization",
                List.of("SecurityFilterChain", "JWT", "OncePerRequestFilter", "401 vs 403"),
                List.of(
                    new ScenarioStep(1, "No token", new RequestExample("GET", "/api/orders", Map.of(), "", "Returns 401")),
                    new ScenarioStep(2, "User token on admin endpoint", new RequestExample("POST", "/api/products", Map.of("Authorization", "Bearer {{user1_token}}"), "{\"sku\":\"NEW\",\"name\":\"A\",\"price\":1,\"stock\":1,\"categoryId\":1}", "Returns 403")),
                    new ScenarioStep(3, "Cross-user access", new RequestExample("GET", "/api/orders/2", Map.of("Authorization", "Bearer {{user2_token}}"), "", "Returns 403 (accessing other's order)"))
                ),
                new BridgeInfo("Passport middleware", "AddAuthentication"),
                List.of("Storing state in a stateless JWT setup"),
                List.of("What is the difference between 401 and 403?"),
                List.of(
                    new SourceRef("Security Config", "com/example/explorer/security/SecurityConfig.java", null, null),
                    new SourceRef("JWT Filter", "com/example/explorer/security/JwtAuthenticationFilter.java", null, null)
                )
            ),
            new ScenarioInfo("cancel-shipped", "Cancel a shipped order", "State machine patterns",
                List.of("Enum with methods", "switch expressions", "state machine"),
                List.of(
                    new ScenarioStep(1, "Cancel shipped order", new RequestExample("PATCH", "/api/orders/1/cancel", Map.of("Authorization", "Bearer {{admin_token}}"), "", "Fails with 409 Invalid Transition"))
                ),
                new BridgeInfo("TS enum (limited)", "C# enum + extension methods"),
                List.of("Leaking state transition logic into the service instead of keeping it in Enum or Entity"),
                List.of("How can Java enums contain behavior?"),
                List.of(
                    new SourceRef("Order Status", "com/example/explorer/domain/model/OrderStatus.java", null, null)
                )
            ),
            new ScenarioInfo("profiles-jpa", "Switch from mock to JPA", "Spring Profiles",
                List.of("@Profile", "application-{profile}.yml", "Spring Data JPA"),
                List.of(),
                new BridgeInfo("NODE_ENV", "appsettings.{env}.json"),
                List.of("Leaving test db credentials in default profile"),
                List.of("How do you activate a specific Spring profile?"),
                List.of(
                    new SourceRef("JPA Repository Reference", "resources/reference/JpaProductRepository.java.ref", null, null)
                )
            ),
            new ScenarioInfo("actuator-health", "Health and metrics", "Observability",
                List.of("Actuator", "health indicators", "Micrometer"),
                List.of(
                    new ScenarioStep(1, "Health check", new RequestExample("GET", "/actuator/health", Map.of(), "", "Shows UP")),
                    new ScenarioStep(2, "Metrics", new RequestExample("GET", "/actuator/metrics", Map.of(), "", "Shows available metrics"))
                ),
                new BridgeInfo("express-status-monitor", "HealthChecks"),
                List.of("Exposing sensitive endpoints without security"),
                List.of("What is Spring Boot Actuator?"),
                List.of()
            )
        );
    }

    @GetMapping
    public List<ScenarioInfo> getScenarios() {
        return scenarios;
    }
}
