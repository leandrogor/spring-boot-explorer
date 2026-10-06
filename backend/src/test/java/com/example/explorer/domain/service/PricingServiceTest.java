package com.example.explorer.domain.service;

import com.example.explorer.domain.model.Customer;
import com.example.explorer.domain.model.CustomerType;
import com.example.explorer.domain.model.Order;
import com.example.explorer.domain.model.OrderLine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    @Test
    void regularCustomerNoVolumeDiscount() {
        Customer customer = new Customer(1L, "Test", "test@test.com", CustomerType.REGULAR, "user1");
        Order order = createOrderWithItems(5); // < 10 units
        
        BigDecimal discount = pricingService.calculateDiscountRate(order, customer);
        assertEquals(0, discount.compareTo(BigDecimal.ZERO));
    }

    @Test
    void premiumCustomerGets5Percent() {
        Customer customer = new Customer(1L, "Test", "test@test.com", CustomerType.PREMIUM, "user1");
        Order order = createOrderWithItems(5);
        
        BigDecimal discount = pricingService.calculateDiscountRate(order, customer);
        assertEquals(0, discount.compareTo(new BigDecimal("0.05")));
    }

    @Test
    void volumeDiscountAt10Units() {
        Customer customer = new Customer(1L, "Test", "test@test.com", CustomerType.REGULAR, "user1");
        Order order = createOrderWithItems(10);
        
        BigDecimal discount = pricingService.calculateDiscountRate(order, customer);
        assertEquals(0, discount.compareTo(new BigDecimal("0.03")));
    }

    @Test
    void premiumPlusVolumeIsMultiplicative() {
        Customer customer = new Customer(1L, "Test", "test@test.com", CustomerType.PREMIUM, "user1");
        Order order = createOrderWithItems(10);
        
        BigDecimal discount = pricingService.calculateDiscountRate(order, customer);
        // 1 - (1 - 0.05) * (1 - 0.03) = 1 - (0.95 * 0.97) = 1 - 0.9215 = 0.0785
        assertEquals(0, discount.compareTo(new BigDecimal("0.0785")));
    }

    private Order createOrderWithItems(int totalQuantity) {
        OrderLine line = new OrderLine(1L, "SKU-1", "Product 1", totalQuantity, new BigDecimal("10.00"));
        return new Order(1L, List.of(line));
    }
}
