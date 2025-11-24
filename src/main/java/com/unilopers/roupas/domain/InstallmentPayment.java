package com.unilopers.roupas.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_installment_payment")
@JacksonXmlRootElement(localName = "installmentPayment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentPayment {
    @Id
    @Column(name = "installment_payment_id", length = 36)
    @JacksonXmlProperty(localName = "id")
    private String id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JacksonXmlProperty(localName = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "order_id", length = 36)
    @JacksonXmlProperty(localName = "orderId")
    private String orderId;

    @Column(name = "installment_number")
    @JacksonXmlProperty(localName = "installmentNumber")
    private Integer installmentNumber;

    @Column(name = "amount")
    @JacksonXmlProperty(localName = "amount")
    private Double amount;

    @Column(name = "maturity")
    @JacksonXmlProperty(localName = "maturity")
    private LocalDate maturity;

    @Column(name = "paid")
    @JacksonXmlProperty(localName = "paid")
    private Boolean paid;

    @Column(name = "payment_date")
    @JacksonXmlProperty(localName = "paymentDate")
    private LocalDate paymentDate;

    @Column(name = "method", length = 255)
    @JacksonXmlProperty(localName = "method")
    private String method;
}
