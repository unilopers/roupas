package com.unilopers.roupas.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_order_item")
@JacksonXmlRootElement(localName = "OrderItem")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id")
    @JacksonXmlProperty(localName = "orderItemId")
    private UUID orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_item_order")
    )
    @JacksonXmlProperty(localName = "order")
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_item_product")
    )
    @JacksonXmlProperty(localName = "product")
    private Product product;

    @Column(name = "quantity")
    @JacksonXmlProperty(localName = "quantity")
    private Long quantity;


    @Column(name = "unity_price")
    @JacksonXmlProperty(localName = "unitPrice")
    private Double unitPrice;

    @Column(name = "subtotal")
    @JacksonXmlProperty(localName = "subtotal")
    private Double subtotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}