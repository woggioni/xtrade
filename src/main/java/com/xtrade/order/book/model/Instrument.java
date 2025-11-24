package com.xtrade.order.book.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Instrument implements Serializable {
    @Id
    private String id;
}
