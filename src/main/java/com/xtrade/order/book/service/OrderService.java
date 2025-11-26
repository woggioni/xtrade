package com.xtrade.order.book.service;

import com.xtrade.order.book.exception.OrderBookClosedException;
import com.xtrade.order.book.model.Instrument;
import com.xtrade.order.book.model.Order;
import com.xtrade.order.book.model.OrderBook;
import com.xtrade.order.book.model.Order_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    @PersistenceContext
    private final EntityManager em;

    private final OrderBookService orderBookService;
    private final SecurityContextHolderStrategy schs;

    @Transactional
    public Order createOrder(long quantity, BigDecimal price, Instrument instrument) {
        final var result = new Order();
        final var orderBook = orderBookService.findOrderBook(instrument)
            .orElseGet(() -> orderBookService.createOrderBook(instrument));
        if(orderBook.getStatus() == OrderBook.Status.Closed) {
            final var user = schs.getContext().getAuthentication().getName();
            throw new OrderBookClosedException(
                String.format("Order book for instrument '%s' and user '%s' is closed",
                    instrument.getId(), user
                )
            );
        }
        result.setQuantity(quantity);
        result.setPrice(price);
        result.setOrderBook(
            orderBookService.findOrderBook(instrument)
                .orElseGet(() -> orderBookService.createOrderBook(instrument))
        );
        em.persist(result);
        return result;
    }

    @Transactional
    public List<Order> fetchOrders(OrderBook orderBook) {
        final var cb = em.getCriteriaBuilder();
        final var query = cb.createQuery(Order.class);
        final var entity = query.from(Order.class);
        final var predicate = cb.equal(entity.get(Order_.orderBook), orderBook);
        query.select(entity).where(predicate);
        return em.createQuery(query).getResultList();
    }

    @Transactional
    public Optional<Order> fetchOrder(long orderId) {
        return Optional.ofNullable(em.find(Order.class, orderId));
    }
}
