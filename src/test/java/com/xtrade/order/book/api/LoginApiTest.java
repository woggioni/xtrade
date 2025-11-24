package com.xtrade.order.book.api;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class LoginApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    public void loginTest() {
        mvc.perform(rb ->
                post("/login", null, null)
                    .content("username=user&password=password")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .buildRequest(rb.getContext("localhost:8080"))
            )
            .andExpect(status().isOk())
            .andReturn();
    }
}

