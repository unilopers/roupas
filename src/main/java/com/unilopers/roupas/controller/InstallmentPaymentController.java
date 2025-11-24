package com.unilopers.roupas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unilopers.roupas.domain.InstallmentPayment;
import com.unilopers.roupas.repository.InstallmentPaymentRepository;

@RestController
@RequestMapping(value = "/installment-payments", produces = MediaType.APPLICATION_XML_VALUE)
public class InstallmentPaymentController {

    @Autowired
    private InstallmentPaymentRepository repository;

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public List<InstallmentPayment> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<InstallmentPayment> getById(@PathVariable Long id) {
        Optional<InstallmentPayment> payment = repository.findById(id);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public InstallmentPayment create(@RequestBody InstallmentPayment payment) {
        return repository.save(payment);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<InstallmentPayment> update(@PathVariable Long id, @RequestBody InstallmentPayment payment) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        payment.setId(id.toString());
        return ResponseEntity.ok(repository.save(payment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
