package com.xtrade.order.book.api;

import com.xtrade.order.book.api.payload.PriceAndQuantity;
import com.xtrade.order.book.exception.OrderNotFoundException;
import com.xtrade.order.book.model.Execution;
import com.xtrade.order.book.model.Order;
import com.xtrade.order.book.service.ExecutionService;
import com.xtrade.order.book.service.InstrumentService;
import com.xtrade.order.book.service.OrderBookService;
import com.xtrade.order.book.service.OrderService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/execution")
@RequiredArgsConstructor
public class ExecutionApi {

    private final InstrumentService instrumentService;
    private final OrderBookService orderBookService;
    private final OrderService orderService;
    private final ExecutionService executionService;
    private final SecurityContextHolderStrategy schs;

    @RequestMapping(
        path = "{order}",
        method = RequestMethod.PUT,
        consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Execution> addExecution(
        @PathVariable(name = "order") long orderId,
        @ModelAttribute PriceAndQuantity priceAndQuantity
    ) {
        Order order = orderService.fetchOrder(orderId)
            .orElseThrow(() -> {
                    final var sctx = schs.getContext();
                    final var user = sctx.getAuthentication().getName();
                    return new OrderNotFoundException(
                        String.format(
                            "Order with id '%d' not found for user '%s'",
                            orderId,
                            user
                        )
                    );
                }
            );
        final var execution = executionService.addExecution(
            order,
            priceAndQuantity.quantity(),
            priceAndQuantity.price()
        );
        return new ResponseEntity<>(execution, HttpStatus.OK);
    }

    @RequestMapping(
        path = "{order}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Execution>> listExecutions(
        @PathVariable(name = "order") long orderId
    ) {
        return orderService.fetchOrder(orderId)
            .map(executionService::fetchExecutions)
            .map(res -> new ResponseEntity<>(res, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Execution>> listExecutions(
        @RequestParam(name = "order", required = false)
        @Parameter(example = "1")
        Long orderId,
        @Parameter(example = "CH0031240127")
        @RequestParam(name = "instrument", required = false)
        String instrumentId
    ) {
        return Optional.ofNullable(instrumentId)
            .map(instrumentService::getInstrument)
            .flatMap(orderBookService::findOrderBook)
            .map(orderService::fetchOrders)
            .map(executionService::fetchExecutions)
            .or(() ->
                orderService.fetchOrder(orderId).map(executionService::fetchExecutions)
            ).map(res -> new ResponseEntity<>(res, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
