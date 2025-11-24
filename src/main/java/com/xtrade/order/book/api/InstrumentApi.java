package com.xtrade.order.book.api;

import com.xtrade.order.book.model.Instrument;
import com.xtrade.order.book.service.InstrumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instrument")
@RequiredArgsConstructor
public class InstrumentApi {

    private final InstrumentService instrumentService;

    @RequestMapping(
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Instrument>> listInstruments() {
        return new ResponseEntity<>(instrumentService.getAllInstruments(), HttpStatus.OK);
    }
}
