package com.unilopers.roupas.repository;

import com.unilopers.roupas.domain.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    
    List<InventoryMovement> findByProductId(UUID productId);
    
    List<InventoryMovement> findByType(InventoryMovement.MovementType type);
}
