package com.example.explorer.web.error;

import com.example.explorer.domain.exception.BusinessException;
import com.example.explorer.domain.exception.DuplicateSkuException;
import com.example.explorer.domain.exception.InsufficientStockException;
import com.example.explorer.domain.exception.InvalidTransitionException;
import com.example.explorer.domain.exception.OrderAccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice es el equivalente a un error-handling middleware global en Express o IExceptionHandler en ASP.NET Core.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error de validación");
        problem.setTitle("Error de validación");
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Stock insuficiente");
        problem.setProperty("productId", ex.getProductId());
        problem.setProperty("available", ex.getAvailable());
        problem.setProperty("requested", ex.getRequested());
        problem.setProperty("code", ex.getCode());
        return problem;
    }

    @ExceptionHandler(InvalidTransitionException.class)
    public ProblemDetail handleInvalidTransition(InvalidTransitionException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Transición de estado inválida");
        problem.setProperty("currentStatus", ex.getCurrentStatus());
        problem.setProperty("targetStatus", ex.getTargetStatus());
        problem.setProperty("code", ex.getCode());
        return problem;
    }

    @ExceptionHandler(DuplicateSkuException.class)
    public ProblemDetail handleDuplicateSku(DuplicateSkuException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("SKU duplicado");
        problem.setProperty("sku", ex.getSku());
        problem.setProperty("code", ex.getCode());
        return problem;
    }

    @ExceptionHandler(OrderAccessDeniedException.class)
    public ProblemDetail handleOrderAccessDenied(OrderAccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Acceso denegado al pedido");
        problem.setProperty("orderId", ex.getOrderId());
        problem.setProperty("userId", ex.getUserId());
        problem.setProperty("code", ex.getCode());
        return problem;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
        return ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
        problem.setTitle("Error Interno");
        return problem;
    }
}
