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
@Table(name = "tb_order")
@JacksonXmlRootElement(localName = "order")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    @JacksonXmlProperty(localName = "orderId")
    private UUID orderId;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "notes")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
    private User user;
}
