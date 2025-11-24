package com.xtrade.order.book.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Execution {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(optional = false)
    private Order order;

    @Column(nullable = false)
    private long filledQuantity;

    @Column(nullable = false)
    private BigDecimal price;
}
