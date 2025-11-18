package com.unilopers.roupas.repository;

import com.unilopers.roupas.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Orders, UUID> {
    List<Orders> findByUser_UserId(UUID userId);
}
