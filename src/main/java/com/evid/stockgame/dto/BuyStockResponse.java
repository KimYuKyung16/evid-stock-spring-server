package com.evid.stockgame.dto;

public record BuyStockResponse(
        int executedPrice,
        String message
) {}
