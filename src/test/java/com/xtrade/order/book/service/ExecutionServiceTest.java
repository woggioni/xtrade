package com.xtrade.order.book.service;

import com.xtrade.order.book.exception.OrderBookOpenException;
import com.xtrade.order.book.misc.Tuple2;
import com.xtrade.order.book.model.Execution;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@WithMockUser(value = "user")
public class ExecutionServiceTest {

    private static final String INSTRUMENT_ID = "US5949181045";
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private OrderBookService orderBookService;
    @Autowired
    private OrderService orderService;

    @Autowired
    private ExecutionService executionService;

    private Instrument instrument;
    private OrderBook orderBook;
    private List<Order> orders;
    private Execution execution;

    public void createOrders() {
        instrument = instrumentService.getInstrument(INSTRUMENT_ID);
        orders = Stream.of(
            Tuple2.newInstance(100, BigDecimal.valueOf(0.55)),
            Tuple2.newInstance(200, BigDecimal.valueOf(0.52)),
            Tuple2.newInstance(80, (BigDecimal) null),
            Tuple2.newInstance(60, BigDecimal.valueOf(0.58))
        ).map(t -> orderService.createOrder(t.get_1(), t.get_2(), instrument))
            .collect(Collectors.toList());
    }

    @Test
    @SneakyThrows
    public void addExecutionToOpenBook() {
        createOrders();
        final var order = orders.get(1);
        Assertions.assertThrows(OrderBookOpenException.class, () -> {
            executionService.addExecution(order, 10, BigDecimal.valueOf(0.24));
        });
    }

    @Test
    public void closeOrderBook() {
        createOrders();
        orderBookService.closeOrderBook(instrument);
        orderBook = orderBookService.findOrderBook(instrument).orElse(null);
        Assertions.assertNotNull(orderBook);
        Assertions.assertEquals(OrderBook.Status.Closed, orderBook.getStatus());
    }

    @Test
    @SneakyThrows
    public void addExecution() {
        closeOrderBook();
        final var order = orders.get(1);
        execution = executionService.addExecution(order, 10, BigDecimal.valueOf(0.24));
        Assertions.assertNotNull(execution);
    }

    @Test
    @SneakyThrows
    public void fetchExecution() {
        addExecution();
        final var order = orders.get(1);
        final var executions = executionService.fetchExecutions(order);
        Assertions.assertNotNull(executions);
        Assertions.assertEquals(Collections.singletonList(execution), executions);
        final var executionsByInstrument = executionService.fetchExecutions(orderService.fetchOrders(orderBook));
        Assertions.assertNotNull(executionsByInstrument);
        Assertions.assertEquals(Collections.singletonList(execution), executions);
    }
}
