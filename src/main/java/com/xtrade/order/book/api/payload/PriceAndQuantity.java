package com.xtrade.order.book.api.payload;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record PriceAndQuantity(
    @Schema(example="0.55")
    BigDecimal price,
    @Schema(example="200")
    long quantity
) { }
