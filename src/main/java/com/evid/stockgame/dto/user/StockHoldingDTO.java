package com.evid.stockgame.dto.user;

public record StockHoldingDTO(
        String com_name,
        double buy_average,
        int count,
        double percent,
        double current_price,
        double difference_price,
        double difference_percent
) {
}

