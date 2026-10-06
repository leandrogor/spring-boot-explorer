package com.example.explorer.web.controller;

import com.example.explorer.domain.service.OrderService;
import com.example.explorer.web.dto.OrderRequest;
import com.example.explorer.web.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderRequest request) {
        return orderService.create(request, getCurrentUsername());
    }

    @GetMapping
    public List<OrderResponse> findAll() {
        return orderService.findAll(getCurrentUsername(), isCurrentUserAdmin());
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id) {
        return orderService.findById(id, getCurrentUsername(), isCurrentUserAdmin());
    }

    @PatchMapping("/{id}/confirm")
    public OrderResponse confirm(@PathVariable Long id) {
        return orderService.confirm(id, getCurrentUsername());
    }

    @PatchMapping("/{id}/pay")
    public OrderResponse pay(@PathVariable Long id) {
        return orderService.pay(id, getCurrentUsername());
    }

    @PatchMapping("/{id}/ship")
    public OrderResponse ship(@PathVariable Long id) {
        return orderService.ship(id);
    }

    @PatchMapping("/{id}/deliver")
    public OrderResponse deliver(@PathVariable Long id) {
        return orderService.deliver(id);
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id) {
        return orderService.cancel(id, getCurrentUsername(), isCurrentUserAdmin());
    }
}
