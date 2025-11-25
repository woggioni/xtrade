package com.xtrade.order.book.service;

import com.xtrade.order.book.exception.OrderBookNotFoundException;
import com.xtrade.order.book.model.Instrument;
import com.xtrade.order.book.model.OrderBook;
import com.xtrade.order.book.model.OrderBook_;
import com.xtrade.order.book.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderBookService {

    @PersistenceContext
    private final EntityManager em;

    private final SecurityContextHolderStrategy schs;

    @Transactional
    public OrderBook createOrderBook(Instrument instrument) {
        final var orderBook = new OrderBook();
        final var sctx = schs.getContext();
        orderBook.setUser((User) sctx.getAuthentication().getPrincipal());
        orderBook.setInstrument(instrument);
        orderBook.setStatus(OrderBook.Status.Open);
        em.persist(orderBook);
        return orderBook;
    }

    public Optional<OrderBook> findOrderBook(Instrument instrument) {
        final var sctx = schs.getContext();
        final var user = sctx.getAuthentication().getPrincipal();
        final var cb = em.getCriteriaBuilder();
        final var query = cb.createQuery(OrderBook.class);
        final var orderBookEntity = query.from(OrderBook.class);
        final var predicate = cb.and(
            cb.equal(orderBookEntity.get(OrderBook_.user), user),
            cb.equal(orderBookEntity.get(OrderBook_.instrument), instrument)
        );
        query.select(orderBookEntity).where(predicate);
        final var result = em.createQuery(query)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
        return result;
    }

    @Transactional
    public OrderBook openOrderBook(Instrument instrument) {
        final var orderBook = findOrderBook(instrument)
            .orElseGet(() -> createOrderBook(instrument));
        orderBook.setStatus(OrderBook.Status.Open);
        em.persist(orderBook);
        return orderBook;
    }

    @Transactional
    public void closeOrderBook(Instrument instrument) {
        final var sctx = schs.getContext();
        final var user = sctx.getAuthentication().getName();
        final var orderBook = findOrderBook(instrument)
            .orElseThrow(() -> new OrderBookNotFoundException(
                String.format(
                    "Order book for instrument '%s' not found for user '%s'",
                    instrument.getId(), user
                )
            )
        );
        orderBook.setStatus(OrderBook.Status.Closed);
        em.persist(orderBook);
    }
}
