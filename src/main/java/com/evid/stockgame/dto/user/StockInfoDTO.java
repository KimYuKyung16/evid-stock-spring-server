package com.evid.stockgame.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockInfoDTO {
    private String comName;
    private int buyAverage;
    private int count;
    private double percent;
    private int currentPrice;
    private int differencePrice;
    private double differencePercent;
}
