package com.example.explorer.domain.service;

import com.example.explorer.domain.model.Customer;
import com.example.explorer.domain.model.CustomerType;
import com.example.explorer.domain.model.Order;
import com.example.explorer.domain.model.OrderLine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {

    public BigDecimal calculateDiscountRate(Order order, Customer customer) {
        // En Java y Spring, los cálculos financieros NUNCA deben usar tipos double o float debido
        // a la pérdida de precisión en aritmética de punto flotante. Siempre utilizamos BigDecimal.
        boolean isPremium = customer.getType() == CustomerType.PREMIUM;
        boolean isVolume = order.getLines().stream()
                                .mapToInt(OrderLine::getQuantity)
                                .sum() >= 10;

        if (isPremium && isVolume) {
            BigDecimal premiumRate = new BigDecimal("0.05");
            BigDecimal volumeRate = new BigDecimal("0.03");
            
            // Fórmula: 1 - (1 - 0.05) * (1 - 0.03)
            BigDecimal pFactor = BigDecimal.ONE.subtract(premiumRate);
            BigDecimal vFactor = BigDecimal.ONE.subtract(volumeRate);
            return BigDecimal.ONE.subtract(pFactor.multiply(vFactor))
                                 .setScale(4, RoundingMode.HALF_UP);
        } else if (isPremium) {
            return new BigDecimal("0.05");
        } else if (isVolume) {
            return new BigDecimal("0.03");
        }

        return BigDecimal.ZERO;
    }

    public void applyPricing(Order order, Customer customer) {
        BigDecimal rate = calculateDiscountRate(order, customer);
        order.applyPricing(rate);
    }
}
