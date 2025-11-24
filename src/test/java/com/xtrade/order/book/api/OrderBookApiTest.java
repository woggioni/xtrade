package com.xtrade.order.book.api;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(username = "user", password = "password", roles="USER")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class OrderBookApiTest {
    private static final String INSTRUMENT_ID = "US5949181045";
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    public void createOrderBook() {
        mvc.perform(rb ->
                put("/api/order-book/" + INSTRUMENT_ID, null, null)
                    .buildRequest(rb.getContext("localhost:8080"))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    @SneakyThrows
    public void closeOrderBook() {
        createOrderBook();
        mvc.perform(rb ->
                post("/api/order-book/" + INSTRUMENT_ID, null, null)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content("status=Closed")
                    .buildRequest(rb.getContext("localhost:8080"))
            )
            .andExpect(status().isOk())
            .andReturn();
    }
}

