package com.example.explorer.explorer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/explorer/architecture")
public class ArchitectureController {

    public record ArchitectureLayer(
            String id,
            String name,
            String description,
            String expressEquivalent,
            String aspnetEquivalent,
            List<ComponentBean> components
    ) {}

    public record ComponentBean(
            String name,
            String type,
            String annotation,
            String sourceFile,
            String role
    ) {}

    @GetMapping
    public List<ArchitectureLayer> getArchitecture() {
        return List.of(
            new ArchitectureLayer(
                "security-filter",
                "Filtros de Servlet & Seguridad",
                "Punto de entrada de cada request HTTP. Intercepta, traza y autentica JWT.",
                "Express Middlewares (app.use(trace), app.use(passport))",
                "ASP.NET Middleware Pipeline (UseAuthentication, Custom Middlewares)",
                List.of(
                    new ComponentBean("RequestTraceFilter", "Filter", "@Component", "com/example/explorer/tracing/RequestTraceFilter.java", "Genera X-Trace-Id y captura pasos del ciclo de vida"),
                    new ComponentBean("JwtAuthenticationFilter", "OncePerRequestFilter", "@Component", "com/example/explorer/security/JwtAuthenticationFilter.java", "Extrae y valida token Bearer JWT"),
                    new ComponentBean("SecurityConfig", "SecurityFilterChain", "@Configuration", "com/example/explorer/security/SecurityConfig.java", "Define reglas de autorización por endpoint y rol")
                )
            ),
            new ArchitectureLayer(
                "web-controller",
                "Capa Web / Controladores",
                "Deserializa JSON, valida DTOs mediante anotaciones y mapea respuestas HTTP (RFC 7807).",
                "Express Routes & Controllers (req, res)",
                "ASP.NET API Controllers ([ApiController], [Route])",
                List.of(
                    new ComponentBean("ProductController", "RestController", "@RestController", "com/example/explorer/web/controller/ProductController.java", "Expone catálogo con paginación y filtros"),
                    new ComponentBean("OrderController", "RestController", "@RestController", "com/example/explorer/web/controller/OrderController.java", "Gestiona pedidos con control de roles"),
                    new ComponentBean("AuthController", "RestController", "@RestController", "com/example/explorer/web/controller/AuthController.java", "Login y emisión de tokens JWT"),
                    new ComponentBean("GlobalExceptionHandler", "ControllerAdvice", "@RestControllerAdvice", "com/example/explorer/web/error/GlobalExceptionHandler.java", "Captura excepciones y produce ProblemDetail")
                )
            ),
            new ArchitectureLayer(
                "domain-service",
                "Capa de Negocio / Servicios",
                "Reglas de negocio puras, cálculos monetarios con BigDecimal, transiciones de estado y disparo de eventos.",
                "Service Layer / Domain Modules (TS)",
                "Application / Domain Services (.NET)",
                List.of(
                    new ComponentBean("ProductService", "Service", "@Service", "com/example/explorer/domain/service/ProductService.java", "Validación de SKU, gestión de stock y filtros"),
                    new ComponentBean("OrderService", "Service", "@Service", "com/example/explorer/domain/service/OrderService.java", "Ciclo de vida de pedidos, reservas y cancelación"),
                    new ComponentBean("PricingService", "Service", "@Service", "com/example/explorer/domain/service/PricingService.java", "Cálculo de descuentos con BigDecimal")
                )
            ),
            new ArchitectureLayer(
                "domain-events",
                "Eventos de Dominio Asíncronos",
                "Desacopla efectos secundarios (notificaciones) de la transacción principal.",
                "Node EventEmitter / BullMQ / RabbitMQ",
                "MediatR Notifications / BackgroundService",
                List.of(
                    new ComponentBean("OrderNotificationListener", "EventListener", "@Component + @Async", "com/example/explorer/event/OrderNotificationListener.java", "Simula envío asíncrono de emails en Thread Pool")
                )
            ),
            new ArchitectureLayer(
                "repository",
                "Capa de Persistencia",
                "Abstracción de acceso a datos. Implementación InMemory (ConcurrentHashMap) con switch a Spring Data JPA por perfil.",
                "Repository Pattern / Prisma / TypeORM Repositories",
                "Repository Pattern / Entity Framework Core DbContext",
                List.of(
                    new ComponentBean("ProductRepository", "Interface", "Repository", "com/example/explorer/repository/ProductRepository.java", "Contrato de persistencia de productos"),
                    new ComponentBean("InMemoryProductRepository", "Repository", "@Repository + @Profile(!jpa)", "com/example/explorer/repository/inmemory/InMemoryProductRepository.java", "Almacenamiento en ConcurrentHashMap"),
                    new ComponentBean("SeedDataInitializer", "CommandLineRunner", "@Component", "com/example/explorer/repository/inmemory/SeedDataInitializer.java", "Carga datos iniciales al arrancar la app")
                )
            )
        );
    }
}
