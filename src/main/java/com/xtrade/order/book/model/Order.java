package com.xtrade.order.book.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "ob_order")
public class Order {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(optional = false)
    private OrderBook orderBook;

    @Column(nullable = true)
    private BigDecimal price;

    private long quantity;

    public enum Type {
        LIMIT, MARKET
    }

    public Type getType() {
        if(price == null) {
            return Type.MARKET;
        } else {
            return Type.LIMIT;
        }
    }
}
