package com.example.explorer.repository.inmemory;

import com.example.explorer.domain.model.*;
import com.example.explorer.repository.CategoryRepository;
import com.example.explorer.repository.CustomerRepository;
import com.example.explorer.repository.OrderRepository;
import com.example.explorer.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
@Profile("!jpa")
public class SeedDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public SeedDataInitializer(ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               CustomerRepository customerRepository,
                               OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        resetData();
    }

    public void resetData() {
        productRepository.clear();
        categoryRepository.clear();
        customerRepository.clear();
        orderRepository.clear();

        categoryRepository.save(new Category(1L, "Electrónica", "Dispositivos y componentes electrónicos"));
        categoryRepository.save(new Category(2L, "Periféricos", "Accesorios y periféricos de computación"));
        categoryRepository.save(new Category(3L, "Software", "Licencias y suscripciones de software"));

        productRepository.save(new Product(1L, "ELEC-001", "Laptop Pro 15", new BigDecimal("1299.99"), 10, 1L));
        productRepository.save(new Product(2L, "ELEC-002", "Monitor 4K 27\"", new BigDecimal("549.99"), 25, 1L));
        productRepository.save(new Product(3L, "PERI-001", "Teclado Mecánico", new BigDecimal("129.99"), 50, 2L));
        productRepository.save(new Product(4L, "PERI-002", "Mouse Ergonómico", new BigDecimal("79.99"), 100, 2L));
        productRepository.save(new Product(5L, "SOFT-001", "IDE License Annual", new BigDecimal("199.99"), 999, 3L));

        customerRepository.save(new Customer(1L, "Acme Corp", "admin@acme.com", CustomerType.PREMIUM, "user1"));
        customerRepository.save(new Customer(2L, "Globex Inc", "info@globex.com", CustomerType.REGULAR, "user2"));

        Order order = new Order(1L, Arrays.asList(
                new OrderLine(1L, "ELEC-001", "Laptop Pro 15", 1, new BigDecimal("1299.99")),
                new OrderLine(3L, "PERI-001", "Teclado Mecánico", 2, new BigDecimal("129.99"))
        ));
        order.applyPricing(BigDecimal.ZERO);
        orderRepository.save(order);
    }
}
