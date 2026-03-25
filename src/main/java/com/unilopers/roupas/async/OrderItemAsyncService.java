package com.unilopers.roupas.async;

import com.unilopers.roupas.domain.OrderItem;
import com.unilopers.roupas.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemAsyncService {

    private final OrderItemRepository orderItemRepository;

    @Async
    public void calculateSubtotalAsync(UUID orderItemId) {
        log.info("[ASYNC] Iniciando cálculo de subtotal para OrderItem {}", orderItemId);

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElse(null);

        if (item == null) {
            log.warn("[ASYNC] OrderItem {} não encontrado para cálculo de subtotal", orderItemId);
            return;
        }

        Double subtotal = item.getQuantity() * item.getUnitPrice();
        item.setSubtotal(subtotal);
        orderItemRepository.save(item);

        log.info("[ASYNC] Subtotal calculado para OrderItem {}: {} x {} = {}",
                orderItemId, item.getQuantity(), item.getUnitPrice(), subtotal);
    }
}
