package com.unilopers.roupas.controller;

import com.unilopers.roupas.domain.Product;
import com.unilopers.roupas.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> create(@RequestBody Product product) {
        try {
            Product entity = productRepository.save(product);
            URI uri = URI.create("/api/product/" + entity.getProductId());
            return ResponseEntity.created(uri).body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> read() {
        try {
            List<Product> entities = productRepository.findAll();
            return ResponseEntity.ok().body(entities);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> readById(@PathVariable UUID id) {
        try {
            Product entity = productRepository.findById(id).orElse(null);
            if (entity == null) {
                return new ResponseEntity<>("Produto não encontrado", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Product product) {
        try {
            Product entity = productRepository.findById(id).orElse(null);
            if (entity == null) {
                return new ResponseEntity<>("Produto não encontrado", HttpStatus.NOT_FOUND);
            }
            entity.setName(product.getName());
            entity.setCategory(product.getCategory());
            entity.setColor(product.getColor());
            entity.setSize(product.getSize());
            entity.setPrice(product.getPrice());
            entity.setActive(product.getActive());

            productRepository.save(entity);
            return ResponseEntity.ok().body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return new ResponseEntity<>("Produto não encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
