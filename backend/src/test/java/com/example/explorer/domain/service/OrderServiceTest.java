package com.example.explorer.domain.service;

import com.example.explorer.domain.event.OrderConfirmedEvent;
import com.example.explorer.domain.exception.InsufficientStockException;
import com.example.explorer.domain.exception.InvalidTransitionException;
import com.example.explorer.domain.model.*;
import com.example.explorer.repository.CustomerRepository;
import com.example.explorer.repository.OrderRepository;
import com.example.explorer.repository.ProductRepository;
import com.example.explorer.web.dto.OrderResponse;
import com.example.explorer.web.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private PricingService pricingService;
    @Mock private OrderMapper orderMapper;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void confirmOrder_reservesStockAndPublishesEvent() {
        Customer customer = new Customer(1L, "Acme", "acme@test.com", CustomerType.PREMIUM, "user1");
        OrderLine line = new OrderLine(100L, "SKU-100", "Product 100", 2, new BigDecimal("10.00"));
        Order order = new Order(1L, List.of(line));
        order.setId(1L);

        Product product = new Product(100L, "SKU-100", "Product 100", new BigDecimal("10.00"), 5, 1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(customerRepository.findByUserId("user1")).thenReturn(Optional.of(customer));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class), any())).thenReturn(
            new OrderResponse(1L, 1L, "Acme", "CONFIRMED", List.of(), BigDecimal.valueOf(20), BigDecimal.ZERO, BigDecimal.valueOf(20), Instant.now())
        );

        orderService.confirm(1L, "user1");

        verify(productRepository).save(product);
        assertEquals(3, product.getStock());
        verify(eventPublisher).publishEvent(any(OrderConfirmedEvent.class));
    }

    @Test
    void confirmOrder_insufficientStock_throwsException() {
        Customer customer = new Customer(1L, "Acme", "acme@test.com", CustomerType.PREMIUM, "user1");
        OrderLine line = new OrderLine(100L, "SKU-100", "Product 100", 5, new BigDecimal("10.00"));
        Order order = new Order(1L, List.of(line));
        order.setId(1L);

        Product product = new Product(100L, "SKU-100", "Product 100", new BigDecimal("10.00"), 2, 1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(customerRepository.findByUserId("user1")).thenReturn(Optional.of(customer));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> orderService.confirm(1L, "user1"));
    }

    @Test
    void cancelConfirmedOrder_restoresStock() {
        Customer customer = new Customer(1L, "Acme", "acme@test.com", CustomerType.PREMIUM, "user1");
        OrderLine line = new OrderLine(100L, "SKU-100", "Product 100", 2, new BigDecimal("10.00"));
        Order order = new Order(1L, List.of(line));
        order.setId(1L);
        order.transitionTo(OrderStatus.CONFIRMED);

        Product product = new Product(100L, "SKU-100", "Product 100", new BigDecimal("10.00"), 3, 1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(customerRepository.findByUserId("user1")).thenReturn(Optional.of(customer));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class), any())).thenReturn(
            new OrderResponse(1L, 1L, "Acme", "CANCELLED", List.of(), BigDecimal.valueOf(20), BigDecimal.ZERO, BigDecimal.valueOf(20), Instant.now())
        );

        orderService.cancel(1L, "user1", false);

        verify(productRepository).save(product);
        assertEquals(5, product.getStock());
    }

    @Test
    void cancelShippedOrder_throwsInvalidTransition() {
        OrderLine line = new OrderLine(100L, "SKU-100", "Product 100", 2, new BigDecimal("10.00"));
        Order order = new Order(1L, List.of(line));
        order.setId(1L);
        order.transitionTo(OrderStatus.CONFIRMED);
        order.transitionTo(OrderStatus.PAID);
        order.transitionTo(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidTransitionException.class, () -> orderService.cancel(1L, "user1", true));
    }
}
