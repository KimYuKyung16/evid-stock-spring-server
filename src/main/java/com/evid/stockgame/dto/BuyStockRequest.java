package com.evid.stockgame.dto;

public record BuyStockRequest(
        int purchase_num,
        String pwd
) {}
