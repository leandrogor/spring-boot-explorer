package com.example.explorer.event;

import com.example.explorer.domain.event.OrderConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

// @EventListener + @Async es el equivalente a EventEmitter en Node.js o MediatR notifications en .NET.
// La diferencia clave: @Async ejecuta en otro thread pool, no en el event loop.
@Component
public class OrderNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationListener.class);

    @Async // Se ejecuta en otro thread — sin @EnableAsync en la app principal, esto se ignora silenciosamente
    @EventListener
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        // Simula envío de notificación con un delay
        try {
            Thread.sleep(500); // Simula latencia de un servicio externo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("📧 Notificación enviada — Pedido #{} confirmado por cliente #{}, total: ${}",
                event.orderId(), event.customerId(), event.total());
    }
}
