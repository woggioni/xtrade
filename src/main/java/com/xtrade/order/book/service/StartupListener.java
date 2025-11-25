package com.xtrade.order.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private final InstrumentService is;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        is.loadValues();
    }
}
