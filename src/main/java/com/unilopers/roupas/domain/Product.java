package com.unilopers.roupas.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", columnDefinition = "VARCHAR(36)")
    private UUID productId;

    @Column(nullable = false)
    private String name;

    private String category;

    private String color;

    private String size;

    private Double price;

    private Boolean active;
}
