package com.xtrade.order.book.api.payload;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserCredentials(
    @Schema(example ="user")
    String username,

    @Schema(example ="password")
    String password) {

}
