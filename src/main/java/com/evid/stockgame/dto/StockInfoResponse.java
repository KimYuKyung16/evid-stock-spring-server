package com.evid.stockgame.dto;

public record StockInfoResponse(
        int currentPrice,
        int previousPrice,
        int difference,
        int percent,
        String companyName
) {}