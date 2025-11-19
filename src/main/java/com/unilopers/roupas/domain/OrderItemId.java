package com.unilopers.roupas.domain;

import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemId implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private UUID orderId;
    private UUID productId;

}
