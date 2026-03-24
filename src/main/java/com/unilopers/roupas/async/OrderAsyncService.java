package com.unilopers.roupas.async;

import com.unilopers.roupas.domain.OrderItem;
import com.unilopers.roupas.domain.Orders;
import com.unilopers.roupas.repository.OrderItemRepository;
import com.unilopers.roupas.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAsyncService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Async("orderTaskExecutor")
    @Transactional
    public void processOrderAsync(UUID orderId) {
        try {
            log.info("Starting async order processing. orderId={}", orderId);

            Orders order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                log.warn("Order not found during async processing. orderId={}", orderId);
                return;
            }

            double baseAmount = calculateBaseAmount(orderId, order.getTotalAmount());
            double discount = sanitizeDiscount(order.getDiscount(), baseAmount);
            double finalAmount = roundMoney(baseAmount - discount);

            order.setDiscount(roundMoney(discount));
            order.setTotalAmount(finalAmount);
            order.setStatus("CONFIRMED");
            orderRepository.save(order);

            log.info(
                    "Async order processing finished. orderId={}, baseAmount={}, discount={}, finalAmount={}",
                    orderId, baseAmount, discount, finalAmount
            );
        } catch (Exception ex) {
            log.error("Error while processing order asynchronously. orderId={}", orderId, ex);
        }
    }

    private double calculateBaseAmount(UUID orderId, Double fallbackTotalAmount) {
        List<OrderItem> items = orderItemRepository.findByOrder_OrderId(orderId);

        double itemsAmount = items.stream()
                .mapToDouble(this::resolveItemAmount)
                .sum();

        double fallbackAmount = fallbackTotalAmount == null ? 0.0 : Math.max(fallbackTotalAmount, 0.0);
        double baseAmount = itemsAmount > 0.0 ? itemsAmount : fallbackAmount;
        return roundMoney(baseAmount);
    }

    private double resolveItemAmount(OrderItem item) {
        if (item.getSubtotal() != null) {
            return Math.max(item.getSubtotal(), 0.0);
        }

        double quantity = item.getQuantity() == null ? 0.0 : item.getQuantity();
        double unitPrice = item.getUnitPrice() == null ? 0.0 : item.getUnitPrice();
        return Math.max(quantity * unitPrice, 0.0);
    }

    private double sanitizeDiscount(Double rawDiscount, double baseAmount) {
        double discount = rawDiscount == null ? 0.0 : rawDiscount;
        if (discount < 0.0) {
            return 0.0;
        }
        return Math.min(discount, baseAmount);
    }

    private double roundMoney(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
