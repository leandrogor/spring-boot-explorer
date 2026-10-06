package com.example.explorer.web.mapper;

import com.example.explorer.domain.model.Order;
import com.example.explorer.domain.model.OrderLine;
import com.example.explorer.domain.model.Product;
import com.example.explorer.web.dto.OrderLineRequest;
import com.example.explorer.web.dto.OrderLineResponse;
import com.example.explorer.web.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order, String customerName) {
        List<OrderLineResponse> lines = order.getLines().stream()
            .map(this::toLineResponse)
            .toList();

        return new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            customerName,
            order.getStatus().name(),
            lines,
            order.getSubtotal(),
            order.getDiscount(),
            order.getTotal(),
            order.getCreatedAt()
        );
    }

    public OrderLine toOrderLine(OrderLineRequest request, Product product) {
        // OrderLine calcula lineTotal internamente en su constructor
        return new OrderLine(
            product.getId(),
            product.getSku(),
            product.getName(),
            request.quantity(),
            product.getPrice()
        );
    }

    public OrderLineResponse toLineResponse(OrderLine line) {
        return new OrderLineResponse(
            line.getProductId(),
            line.getProductSku(),
            line.getProductName(),
            line.getQuantity(),
            line.getUnitPrice(),
            line.getLineTotal()
        );
    }
}
