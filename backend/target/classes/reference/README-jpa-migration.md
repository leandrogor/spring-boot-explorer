# Migración a JPA en 3 Pasos

Esta aplicación utiliza repositorios en memoria (Mock) por defecto para facilitar el aprendizaje y la ejecución sin dependencias externas.

Para cambiar a una base de datos real utilizando Spring Data JPA, sigue estos 3 pasos:

### 1. Agregar dependencias
En tu `pom.xml`, agrega Spring Data JPA y el driver de la base de datos (por ejemplo, H2 en memoria):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Anotar las entidades e Interfaces
Añade las anotaciones `@Entity`, `@Id`, etc. a tus clases de dominio (ver ejemplo en `JpaProductEntity.java.ref`).
Crea interfaces que extiendan `JpaRepository` (ver ejemplo en `JpaProductRepository.java.ref`).

### 3. Activar el perfil JPA
Arranca la aplicación con el perfil de spring `jpa` activo:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=jpa
```

## Comparativa: InMemory vs JPA

**InMemoryProductRepository**
- Implementación manual completa
- Manejo explícito de IDs
- Filtrado manual usando Java Streams
- Datos se pierden al reiniciar

**JpaProductRepository**
- Solo es una interfaz
- IDs generados por base de datos
- Consultas SQL generadas automáticamente
- Posibilidad de persistencia real
