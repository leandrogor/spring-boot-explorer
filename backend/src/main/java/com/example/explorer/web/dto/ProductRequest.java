package com.example.explorer.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank(message = "El SKU es obligatorio")
    String sku,

    @NotBlank(message = "El nombre es obligatorio")
    String name,

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    BigDecimal price,

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    Integer stock,

    @NotNull(message = "La categoría es obligatoria")
    Long categoryId
) {}
