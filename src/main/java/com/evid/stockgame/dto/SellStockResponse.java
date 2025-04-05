package com.evid.stockgame.dto;

public record SellStockResponse(
        int executedPrice,
        String message
) {}
