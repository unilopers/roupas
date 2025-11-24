package com.unilopers.roupas.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_inventory_movement")
@JacksonXmlRootElement(localName = "inventoryMovement")
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inventory_movement_id", columnDefinition = "VARCHAR(36)")
    @JacksonXmlProperty(localName = "id")
    private UUID id;

    @Column(name = "product_id", nullable = false, columnDefinition = "VARCHAR(36)")
    @JacksonXmlProperty(localName = "productId")
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JacksonXmlProperty(localName = "type")
    private MovementType type;

    @Column(nullable = false)
    @JacksonXmlProperty(localName = "quantity")
    private Integer quantity;

    @Column(nullable = false)
    @JacksonXmlProperty(localName = "date")
    private LocalDate date;

    @JacksonXmlProperty(localName = "reason")
    private String reason;

    public enum MovementType {
        IN,
        OUT
    }
}
