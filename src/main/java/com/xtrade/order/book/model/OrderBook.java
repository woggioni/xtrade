package com.xtrade.order.book.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Table(indexes = {
    @Index(columnList = "\"user\", instrument_id", unique = true)
})
@Entity
@Data
public class OrderBook {

    @Id
    @GeneratedValue
    private long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Instrument instrument;

    @Column(nullable = false)
    private Status status;

    public enum Status{
        Open, Closed
    }
}
