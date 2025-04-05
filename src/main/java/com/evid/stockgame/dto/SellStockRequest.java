package com.evid.stockgame.dto;

public record SellStockRequest(
        int sell_num,
        String pwd
) {}