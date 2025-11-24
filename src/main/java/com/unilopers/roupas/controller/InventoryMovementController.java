package com.unilopers.roupas.controller;

import com.unilopers.roupas.domain.InventoryMovement;
import com.unilopers.roupas.repository.InventoryMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory-movements")
public class InventoryMovementController {

    private final InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    public InventoryMovementController(InventoryMovementRepository inventoryMovementRepository) {
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getAllInventoryMovements() {
        try {
            List<InventoryMovement> entities = inventoryMovementRepository.findAll();
            return ResponseEntity.ok().body(entities);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getInventoryMovementById(@PathVariable UUID id) {
        try {
            InventoryMovement entity = inventoryMovementRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inventory Movement not found"));
            return ResponseEntity.ok().body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/product/{productId}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getInventoryMovementsByProductId(@PathVariable UUID productId) {
        try {
            List<InventoryMovement> entities = inventoryMovementRepository.findByProductId(productId);
            return ResponseEntity.ok().body(entities);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/type/{type}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getInventoryMovementsByType(@PathVariable InventoryMovement.MovementType type) {
        try {
            List<InventoryMovement> entities = inventoryMovementRepository.findByType(type);
            return ResponseEntity.ok().body(entities);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createInventoryMovement(@RequestBody InventoryMovement inventoryMovement) {
        try {
            InventoryMovement entity = inventoryMovementRepository.save(inventoryMovement);
            URI uri = URI.create("/api/inventory-movements/" + entity.getId());
            return ResponseEntity.created(uri).body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> updateInventoryMovement(@PathVariable UUID id, @RequestBody InventoryMovement inventoryMovement) {
        try {
            InventoryMovement entity = inventoryMovementRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inventory Movement not found"));
            entity.setProductId(inventoryMovement.getProductId());
            entity.setType(inventoryMovement.getType());
            entity.setQuantity(inventoryMovement.getQuantity());
            entity.setDate(inventoryMovement.getDate());
            entity.setReason(inventoryMovement.getReason());
            inventoryMovementRepository.save(entity);
            return ResponseEntity.ok().body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInventoryMovement(@PathVariable UUID id) {
        try {
            inventoryMovementRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
