package com.xtrade.order.book.api;

import com.xtrade.order.book.api.payload.PriceAndQuantity;
import com.xtrade.order.book.model.Order;
import com.xtrade.order.book.service.InstrumentService;
import com.xtrade.order.book.service.OrderBookService;
import com.xtrade.order.book.service.OrderService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderApi {

    private final OrderBookService orderBookService;
    private final OrderService orderService;

    private final InstrumentService instrumentService;
    @RequestMapping(
        path = "{instrument}",
        method = RequestMethod.GET,
        produces = {
            MediaType.APPLICATION_JSON_VALUE
        }
    )
    public ResponseEntity<List<Order>> fetchOrders(
        @Parameter(example = "CH0031240127")
        @PathVariable(name = "instrument")
        String instrumentId
    ) {
        final var instrument = instrumentService.getInstrument(instrumentId);
        return orderBookService
            .findOrderBook(instrument)
            .map(orderService::fetchOrders)
            .map(sd -> new ResponseEntity<>(sd, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "{instrument}", method = RequestMethod.PUT,
        consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE }
    )
    public ResponseEntity<Order> addOrder(
        @Parameter(example = "CH0031240127")
        @PathVariable(name = "instrument") String instrumentId,
        @ModelAttribute PriceAndQuantity priceAndQuantity
    ) {
        final var instrument = instrumentService.getInstrument(instrumentId);
        final var order = orderService.createOrder(
            priceAndQuantity.quantity(),
            priceAndQuantity.price(),
            instrument
        );
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
