package com.xtrade.order.book.service;

import com.xtrade.order.book.exception.OrderBookNotFoundException;
import com.xtrade.order.book.exception.OrderBookOpenException;
import com.xtrade.order.book.model.Execution;
import com.xtrade.order.book.model.Execution_;
import com.xtrade.order.book.model.Instrument;
import com.xtrade.order.book.model.Order;
import com.xtrade.order.book.model.OrderBook;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ExecutionService {
    @PersistenceContext
    private final EntityManager em;

    private final OrderBookService orderBookService;
    private final SecurityContextHolderStrategy schs;

    @Transactional
    public Execution addExecution(Order order, long filledQuantity, BigDecimal price) {
        Instrument instrument =
            order.getOrderBook().getInstrument();
        final var orderBook =
            orderBookService.findOrderBook(instrument)
                .orElseThrow(() -> {
                    final var user = schs.getContext().getAuthentication().getName();
                    return new OrderBookNotFoundException(
                        String.format(
                            "Order book for instrument '%s' not found for user '%s'",
                            instrument, user
                        )
                    );
                });
        if (orderBook.getStatus() == OrderBook.Status.Open) {
            final var sctx = schs.getContext();
            final var user = sctx.getAuthentication().getName();
            throw new OrderBookOpenException(
                String.format("Order book of user '%s' for instrument '%s' is open",
                    user,
                    orderBook.getInstrument().getId())
            );
        }
        final var result = new Execution();
        result.setFilledQuantity(filledQuantity);
        result.setPrice(price);
        result.setOrder(order);
        em.persist(result);
        return result;
    }

    @Transactional
    public List<Execution> fetchExecutions(Iterable<Order> orders) {
        return fetchExecutions(
            StreamSupport.stream(orders.spliterator(), false).toArray(Order[]::new)
        );
    }

    @Transactional
    public List<Execution> fetchExecutions(Order ...orders) {
        final var cb = em.getCriteriaBuilder();
        final var query = cb.createQuery(Execution.class);
        final var entity = query.from(Execution.class);
        final var predicate = entity.get(Execution_.order).in(orders);
        query.select(entity).where(predicate);
        return em.createQuery(query).getResultList();
    }
}
