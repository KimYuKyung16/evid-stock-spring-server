package com.evid.stockgame.dto.ws.game;

public record StockGraphRequest(
        int companyId,
        String roomCode
) {}
