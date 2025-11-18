package com.unilopers.roupas.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.unilopers.roupas.domain.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
}
