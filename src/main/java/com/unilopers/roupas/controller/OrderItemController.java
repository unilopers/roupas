package com.unilopers.roupas.controller;

import com.unilopers.roupas.async.OrderItemAsyncService;
import com.unilopers.roupas.domain.OrderItem;
import com.unilopers.roupas.domain.Orders;
import com.unilopers.roupas.domain.Product;
import com.unilopers.roupas.repository.OrderItemRepository;
import com.unilopers.roupas.repository.OrderRepository;
import com.unilopers.roupas.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orderitem")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemAsyncService orderItemAsyncService;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_XML_VALUE)
    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    @GetMapping(value = "/by-order", produces = MediaType.APPLICATION_XML_VALUE)
    public List<OrderItem> findByOrder(@RequestParam UUID orderId) {
        return orderItemRepository.findByOrder_OrderId(orderId);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public OrderItem findOne(@PathVariable UUID id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de pedido não encontrado"));
    }

    @PostMapping(
            value = "/create",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItem create(@RequestBody OrderItem body) {

        if (body.getOrder() == null || body.getOrder().getOrderId() == null) {
            throw new RuntimeException("order.orderId é obrigatório");
        }
        if (body.getProduct() == null || body.getProduct().getProductId() == null) {
            throw new RuntimeException("product.productId é obrigatório");
        }

        Orders order = orderRepository.findById(body.getOrder().getOrderId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        Product product = productRepository.findById(body.getProduct().getProductId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(body.getQuantity());
        item.setUnitPrice(body.getUnitPrice());

        // Persistência imediata sem subtotal — resposta rápida ao cliente
        OrderItem saved = orderItemRepository.save(item);

        // Cálculo do subtotal delegado ao processamento assíncrono
        orderItemAsyncService.calculateSubtotalAsync(saved.getOrderItemId());

        return saved;
    }

    @PutMapping(
            value = "/update/{id}",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public OrderItem update(@PathVariable UUID id, @RequestBody OrderItem body) {
        OrderItem entity = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de pedido não encontrado"));

        entity.setQuantity(body.getQuantity());
        entity.setUnitPrice(body.getUnitPrice());

        // Persiste imediatamente sem subtotal
        OrderItem saved = orderItemRepository.save(entity);

        // Recalcula subtotal de forma assíncrona
        orderItemAsyncService.calculateSubtotalAsync(saved.getOrderItemId());

        return saved;
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        if (!orderItemRepository.existsById(id)) {
            throw new RuntimeException("Item de pedido não encontrado");
        }
        orderItemRepository.deleteById(id);
    }
}


