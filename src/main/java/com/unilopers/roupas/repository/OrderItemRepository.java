package com.unilopers.roupas.repository;

import com.unilopers.roupas.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrder_OrderId(UUID orderId);

    List<OrderItem> findByProduct_ProductId(UUID productId);
}

