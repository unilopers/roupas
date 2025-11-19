package com.unilopers.roupas.controller;

import com.unilopers.roupas.domain.InventoryMovement;
import com.unilopers.roupas.repository.InventoryMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory-movements")
public class InventoryMovementController {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @GetMapping("/all")
    public ResponseEntity<List<InventoryMovement>> getAllInventoryMovements() {
        List<InventoryMovement> movements = inventoryMovementRepository.findAll();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryMovement> getInventoryMovementById(@PathVariable UUID id) {
        return inventoryMovementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryMovement>> getInventoryMovementsByProductId(@PathVariable UUID productId) {
        List<InventoryMovement> movements = inventoryMovementRepository.findByProductId(productId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<InventoryMovement>> getInventoryMovementsByType(@PathVariable InventoryMovement.MovementType type) {
        List<InventoryMovement> movements = inventoryMovementRepository.findByType(type);
        return ResponseEntity.ok(movements);
    }

    @PostMapping("/create")
    public ResponseEntity<InventoryMovement> createInventoryMovement(@RequestBody InventoryMovement inventoryMovement) {
        InventoryMovement savedMovement = inventoryMovementRepository.save(inventoryMovement);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovement);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<InventoryMovement> updateInventoryMovement(@PathVariable UUID id, @RequestBody InventoryMovement inventoryMovement) {
        if (!inventoryMovementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        inventoryMovement.setId(id);
        InventoryMovement updatedMovement = inventoryMovementRepository.save(inventoryMovement);
        return ResponseEntity.ok(updatedMovement);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteInventoryMovement(@PathVariable UUID id) {
        if (!inventoryMovementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        inventoryMovementRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
