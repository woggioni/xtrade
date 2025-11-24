package com.xtrade.order.book.service;

import com.xtrade.order.book.model.OrderBook;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(value = "user")
public class OrderBookServiceTest {

    private static final String INSTRUMENT_ID = "US5949181045";
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private OrderBookService orderBookService;
    @Test
    @SneakyThrows
    public void openOrderBook() {
        final var instrument = instrumentService.getInstrument(INSTRUMENT_ID);
        final var orderBook = orderBookService.openOrderBook(instrument);
        Assertions.assertNotNull(orderBook);
        Assertions.assertEquals(OrderBook.Status.Open, orderBook.getStatus());
    }

    @Test
    @SneakyThrows
    public void closeOrderBook() {
        openOrderBook();
        final var instrument = instrumentService.getInstrument(INSTRUMENT_ID);
        orderBookService.closeOrderBook(instrument);
        final var orderBook = orderBookService.findOrderBook(instrument).orElse(null);
        Assertions.assertNotNull(orderBook);
        Assertions.assertEquals(OrderBook.Status.Closed, orderBook.getStatus());
    }
}
