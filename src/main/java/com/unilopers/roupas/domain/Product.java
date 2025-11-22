package com.unilopers.roupas.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
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
@JacksonXmlRootElement(localName = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", columnDefinition = "VARCHAR(36)")
    @JacksonXmlProperty(localName = "productId")
    private UUID productId;

    @Column(nullable = false)
    private String name;

    private String category;

    private String color;

    private String size;

    private Double price;

    private Boolean active;
}
