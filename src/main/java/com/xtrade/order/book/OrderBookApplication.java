package com.xtrade.order.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableWebSecurity
@EnableCaching
@EnableRedisHttpSession
public class OrderBookApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderBookApplication.class, args);
    }
}
