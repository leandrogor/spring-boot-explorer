package com.example.explorer.domain.service;

import com.example.explorer.domain.event.OrderConfirmedEvent;
import com.example.explorer.domain.exception.OrderAccessDeniedException;
import com.example.explorer.domain.model.Customer;
import com.example.explorer.domain.model.Order;
import com.example.explorer.domain.model.OrderLine;
import com.example.explorer.domain.model.OrderStatus;
import com.example.explorer.domain.model.Product;
import com.example.explorer.repository.CustomerRepository;
import com.example.explorer.repository.OrderRepository;
import com.example.explorer.repository.ProductRepository;
import com.example.explorer.web.dto.OrderRequest;
import com.example.explorer.web.dto.OrderResponse;
import com.example.explorer.web.mapper.OrderMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final PricingService pricingService;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderService(OrderRepository orderRepository, 
                        ProductRepository productRepository, 
                        CustomerRepository customerRepository, 
                        PricingService pricingService, 
                        OrderMapper orderMapper, 
                        ApplicationEventPublisher applicationEventPublisher) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.pricingService = pricingService;
        this.orderMapper = orderMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public OrderResponse create(OrderRequest request, String userId) {
        Customer customer = customerRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        List<OrderLine> lines = request.lines().stream()
            .map(lineReq -> {
                Product product = productRepository.findById(lineReq.productId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
                return orderMapper.toOrderLine(lineReq, product);
            })
            .toList();

        // El constructor de Order(customerId, lines) ya setea status=DRAFT, createdAt=now, montos en ZERO
        Order order = new Order(customer.getId(), lines);
        order.applyPricing(BigDecimal.ZERO);
        Order saved = orderRepository.save(order);
        
        return toResponse(saved);
    }

    public OrderResponse findById(Long id, String userId, boolean isAdmin) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!isAdmin) {
            verifyOwnership(order, userId);
        }

        return toResponse(order);
    }

    public List<OrderResponse> findAll(String userId, boolean isAdmin) {
        List<Order> orders;
        if (isAdmin) {
            orders = orderRepository.findAll();
        } else {
            Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
            orders = orderRepository.findByCustomerId(customer.getId());
        }
        return orders.stream().map(this::toResponse).toList();
    }

    public OrderResponse confirm(Long id, String userId) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            
        verifyOwnership(order, userId);
        order.transitionTo(OrderStatus.CONFIRMED);

        for (OrderLine line : order.getLines()) {
            Product product = productRepository.findById(line.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            product.reserveStock(line.getQuantity());
            productRepository.save(product);
        }

        Customer customer = customerRepository.findById(order.getCustomerId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
            
        pricingService.applyPricing(order, customer);
        Order saved = orderRepository.save(order);
        
        applicationEventPublisher.publishEvent(new OrderConfirmedEvent(
            saved.getId(), saved.getCustomerId(), saved.getTotal(), Instant.now()
        ));
        
        return toResponse(saved);
    }

    public OrderResponse pay(Long id, String userId) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            
        verifyOwnership(order, userId);
        order.transitionTo(OrderStatus.PAID);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public OrderResponse ship(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            
        order.transitionTo(OrderStatus.SHIPPED);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public OrderResponse deliver(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            
        order.transitionTo(OrderStatus.DELIVERED);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public OrderResponse cancel(Long id, String userId, boolean isAdmin) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            
        if (!isAdmin) {
            verifyOwnership(order, userId);
        }

        OrderStatus previousStatus = order.getStatus();
        order.transitionTo(OrderStatus.CANCELLED);

        if (previousStatus == OrderStatus.CONFIRMED || previousStatus == OrderStatus.PAID) {
            for (OrderLine line : order.getLines()) {
                Product product = productRepository.findById(line.getProductId()).orElse(null);
                if (product != null) {
                    product.restoreStock(line.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    private void verifyOwnership(Order order, String userId) {
        Customer customer = customerRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        if (!order.getCustomerId().equals(customer.getId())) {
            throw new OrderAccessDeniedException(order.getId(), userId);
        }
    }

    private OrderResponse toResponse(Order order) {
        String customerName = customerRepository.findById(order.getCustomerId())
            .map(Customer::getName)
            .orElse("Unknown");
        return orderMapper.toResponse(order, customerName);
    }
}
