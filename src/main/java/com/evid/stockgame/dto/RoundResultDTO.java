package com.evid.stockgame.dto;

public record RoundResultDTO(
        String name,
        int profile_num,
        int total_price,
        int total_roi,
        int rank
) {}
