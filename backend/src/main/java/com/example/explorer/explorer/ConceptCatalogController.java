package com.example.explorer.explorer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/explorer/concepts")
public class ConceptCatalogController {

    public record ConceptInfo(String id, String name, String category, String description, String bridgeExpress, String bridgeAspNet, List<String> scenarioIds) {}

    private final List<ConceptInfo> concepts;

    public ConceptCatalogController() {
        concepts = List.of(
            new ConceptInfo("controllers", "Controladores REST", "Web", "Manejo de rutas y HTTP", "Express Router", "Controllers en ASP.NET", List.of("list-products", "invalid-product")),
            new ConceptInfo("di", "Inyección de Dependencias", "Core", "Gestión de beans", "Manual", "DI Container", List.of("confirm-order-ok")),
            new ConceptInfo("validation", "Bean Validation", "Web", "Validación de DTOs", "class-validator", "DataAnnotations", List.of("invalid-product")),
            new ConceptInfo("error-handling", "Manejo de Errores", "Web", "@ControllerAdvice y ProblemDetail", "Error Middleware", "Exception Filter", List.of("invalid-product", "confirm-order-no-stock")),
            new ConceptInfo("security", "Spring Security + JWT", "Security", "Autenticación y Autorización", "Passport", "Auth Middleware", List.of("security-access")),
            new ConceptInfo("events", "Eventos de Dominio", "Core", "Publicación y escucha de eventos", "EventEmitter", "MediatR", List.of("confirm-order-ok")),
            new ConceptInfo("profiles", "Perfiles de Spring", "Config", "Configuración por ambiente", "NODE_ENV", "appsettings.env.json", List.of("profiles-jpa")),
            new ConceptInfo("actuator", "Actuator", "Operations", "Métricas y salud", "express-status-monitor", "HealthChecks", List.of("actuator-health")),
            new ConceptInfo("records-dtos", "Records como DTOs", "Java", "Clases inmutables", "Interfaces/Types", "Records", List.of("list-products", "invalid-product", "confirm-order-ok")),
            new ConceptInfo("streams", "Streams API", "Java", "Procesamiento de colecciones", "Array methods", "LINQ", List.of("list-products")),
            new ConceptInfo("bigdecimal", "BigDecimal para importes", "Java", "Precisión para dinero", "decimal.js", "decimal", List.of("confirm-order-ok", "confirm-order-no-stock")),
            new ConceptInfo("enums", "Enums con lógica", "Java", "Máquinas de estado", "enum", "enum + extensions", List.of("cancel-shipped")),
            new ConceptInfo("filters", "Filtros de Servlet", "Web", "Interceptar requests", "Middleware", "Middleware", List.of("security-access")),
            new ConceptInfo("testing", "Testing con JUnit 5", "Testing", "Pruebas unitarias y de integración", "Jest", "xUnit", List.of("list-products", "invalid-product", "confirm-order-ok", "confirm-order-no-stock", "security-access", "cancel-shipped", "profiles-jpa", "actuator-health"))
        );
    }

    @GetMapping
    public List<ConceptInfo> getConcepts() {
        return concepts;
    }

    @GetMapping("/{id}")
    public ConceptInfo getConceptById(@PathVariable String id) {
        return concepts.stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Concepto no encontrado"));
    }
}
