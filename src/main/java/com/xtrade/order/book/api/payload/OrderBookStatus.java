package com.xtrade.order.book.api.payload;

import com.xtrade.order.book.model.OrderBook;

public record OrderBookStatus(OrderBook.Status status) {}
