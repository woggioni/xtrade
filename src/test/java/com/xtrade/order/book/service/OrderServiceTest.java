package com.xtrade.order.book.service;

import com.xtrade.order.book.misc.Tuple2;
import com.xtrade.order.book.model.Instrument;
import com.xtrade.order.book.model.Order;
import com.xtrade.order.book.model.OrderBook;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(value = "user")
public class OrderServiceTest {

    private static final String INSTRUMENT_ID = "US5949181045";
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private OrderBookService orderBookService;
    @Autowired
    private OrderService orderService;

    private OrderBook orderBook = null;
    private List<Order> orders = null;
    private Instrument instrument = null;
    @Test
    @SneakyThrows
    public void createOrders() {
        instrument = instrumentService.getInstrument(INSTRUMENT_ID);
        orderBook = orderBookService.openOrderBook(instrument);
        Assertions.assertNotNull(orderBook);
        orders = Stream.of(
                Tuple2.newInstance(100, BigDecimal.valueOf(0.55)),
                Tuple2.newInstance(200, BigDecimal.valueOf(0.52)),
                Tuple2.newInstance(80, (BigDecimal) null),
                Tuple2.newInstance(60, BigDecimal.valueOf(0.58))
            ).map(t -> orderService.createOrder(t.get_1(), t.get_2(), instrument))
            .collect(Collectors.toList());
        orders.forEach(order -> {
            final var expectedType = order.getPrice() == null ? Order.Type.MARKET : Order.Type.LIMIT;
            Assertions.assertEquals(expectedType, order.getType());
        });
        Assertions.assertEquals(orders, orderService.fetchOrders(orderBook));
    }

    @Test
    @SneakyThrows
    public void fetchOrders() {
        createOrders();
        final var orders = orderService.fetchOrders(orderBook);
        Assertions.assertEquals(this.orders, orders);
    }

    @Test
    @SneakyThrows
    public void addOrders2ClosedBook() {
        createOrders();
        orderBookService.closeOrderBook(instrument);
        orderBook = orderBookService.findOrderBook(instrument).orElse(null);
        Assertions.assertNotNull(orderBook);
        Assertions.assertEquals(OrderBook.Status.Closed, orderBook.getStatus());
        Assertions.assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(20, BigDecimal.ONE, instrument);
        });
    }

}
