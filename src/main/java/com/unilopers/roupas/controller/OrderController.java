package com.unilopers.roupas.controller;

import com.unilopers.roupas.domain.Orders;
import com.unilopers.roupas.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> create(@RequestBody Orders order) {
        try {
            Orders entity = orderRepository.save(order);
            URI uri = URI.create("/api/order/" + entity.getOrderId());
            return ResponseEntity.created(uri).body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> read() {
        try {
            List<Orders> entities = orderRepository.findAll();
            return ResponseEntity.ok().body(entities);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            Orders entity = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            return ResponseEntity.ok().body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Orders order) {
        try {
            Orders entity = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            entity.setCreatedAt(order.getCreatedAt());
            entity.setStatus(order.getStatus());
            entity.setTotalAmount(order.getTotalAmount());
            entity.setDiscount(order.getDiscount());
            entity.setNotes(order.getNotes());
            entity.setUser(order.getUser());
            orderRepository.save(entity);
            return ResponseEntity.ok().body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            orderRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
