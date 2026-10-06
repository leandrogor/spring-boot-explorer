package com.example.explorer.web.controller;

import com.example.explorer.domain.exception.InsufficientStockException;
import com.example.explorer.domain.exception.InvalidTransitionException;
import com.example.explorer.domain.model.OrderStatus;
import com.example.explorer.domain.service.OrderService;
import com.example.explorer.security.JwtService;
import com.example.explorer.security.MockUserDetailsService;
import com.example.explorer.web.dto.OrderResponse;
import com.example.explorer.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MockUserDetailsService mockUserDetailsService;

    @Test
    void confirmOrder_returns200() throws Exception {
        OrderResponse response = new OrderResponse(1L, 1L, "Cust", "CONFIRMED", List.of(), BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN, Instant.now());
        when(orderService.confirm(any(), any())).thenReturn(response);
        
        mockMvc.perform(patch("/api/orders/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void confirmOrder_insufficientStock_returns409() throws Exception {
        when(orderService.confirm(any(), any())).thenThrow(new InsufficientStockException(1L, 2, 5));
        
        mockMvc.perform(patch("/api/orders/1/confirm"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_STOCK"));
    }

    @Test
    void cancelShippedOrder_returns409() throws Exception {
        when(orderService.cancel(any(), any(), any(Boolean.class)))
                .thenThrow(new InvalidTransitionException(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        
        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_TRANSITION"));
    }
}
