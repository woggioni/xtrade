package com.xtrade.order.book.api;

import com.xtrade.order.book.api.payload.OrderBookStatus;
import com.xtrade.order.book.service.InstrumentService;
import com.xtrade.order.book.service.OrderBookService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-book")
@RequiredArgsConstructor
public class OrderBookApi {

    private final OrderBookService orderBookService;

    private final InstrumentService instrumentService;
    @RequestMapping(path = "{instrument}", method = RequestMethod.PUT)
    public void createOrderBook(
        @Parameter(example = "CH0031240127")
        @PathVariable(name = "instrument")
        String id
    ) {
        orderBookService.createOrderBook(instrumentService.getInstrument(id));
    }

    @RequestMapping(path = "{instrument}",
        method = RequestMethod.POST,
        consumes = {
        MediaType.APPLICATION_FORM_URLENCODED_VALUE
    })
    public void updateOrderBook(
        @Parameter(example = "CH0031240127")
        @PathVariable(name = "instrument")
        String id,
        @ModelAttribute
        OrderBookStatus status
    ) {
        final var instrument_obj = instrumentService.getInstrument(id);
        switch (status.status()) {
            case Open -> orderBookService.openOrderBook(instrument_obj);
            case Closed -> orderBookService.closeOrderBook(instrument_obj);
        }
    }
}
