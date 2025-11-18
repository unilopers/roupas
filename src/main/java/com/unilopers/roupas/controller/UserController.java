package com.unilopers.roupas.controller;

import com.unilopers.roupas.domain.User;
import com.unilopers.roupas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody User user) {
        try {
            User entity = userRepository.save(user);
            URI uri = URI.create("/users/" + entity.getUserId());
            return ResponseEntity.created(uri).body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> read() {
        try {
            List<User> entities = userRepository.findAll().stream().toList();
            return ResponseEntity.ok().body(entities);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody User user) {
        try {
            User entity = userRepository.getReferenceById(id);
            entity.setName(user.getName());
            entity.setEmail(user.getEmail());
            entity.setPhone(user.getPhone());
            entity.setRole(user.getRole());
            userRepository.save(entity);
            return ResponseEntity.ok().body(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping( "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
