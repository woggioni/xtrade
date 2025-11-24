package com.xtrade.order.book.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Table(indexes = {
    @Index(columnList = "user_name, instrument_id", unique = true)
})
@Entity
@Data
public class OrderBook {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "user_name", nullable = false)
    private String user;

    @ManyToOne(optional = false)
    private Instrument instrument;

    @Column(nullable = false)
    private Status status;

    public enum Status{
        Open, Closed
    }
}
