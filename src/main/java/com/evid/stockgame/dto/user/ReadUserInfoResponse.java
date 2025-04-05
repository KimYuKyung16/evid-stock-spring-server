package com.evid.stockgame.dto.user;

import com.evid.stockgame.entity.StockPrice;
import com.evid.stockgame.entity.User;
import com.evid.stockgame.entity.UserHoldingStock;
import com.evid.stockgame.entity.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ReadUserInfoResponse(
        UserInfoDTO user_info,
        Map<Integer, StockHoldingDTO> stock_list
) {
    public static ReadUserInfoResponse from(User user, UserInfo info,
                                            List<UserHoldingStock> stocks,
                                            Map<Integer, String> companyNames,
                                            Map<Integer, StockPrice> prices) {
        double totalCurr = 0;
        double totalBuy = 0;
        Map<Integer, StockHoldingDTO> stockList = new HashMap<>();

        for (UserHoldingStock stock : stocks) {
            int companyId = stock.getCompany().getCompanyId();
            double buyPrice = stock.getBuyingPrice();
            int count = stock.getHoldingStockNum();

            StockPrice price = prices.get(companyId);
            double currentPrice = price.getCurrentPrice();

            double percent = ((currentPrice / buyPrice) * 100 - 100);
            double difference = currentPrice - buyPrice;

            totalCurr += currentPrice * count;
            totalBuy += buyPrice * count;

            stockList.put(companyId, new StockHoldingDTO(
                    companyNames.get(companyId),
                    buyPrice,
                    count,
                    percent,
                    currentPrice,
                    difference,
                    percent
            ));
        }

        double roi = totalBuy != 0 ? ((totalCurr - totalBuy) / totalBuy) * 100 : 0;
        double totalAsset = totalCurr + info.getUsingAsset();

        return new ReadUserInfoResponse(
                new UserInfoDTO(
                        user.getUserName(),
                        user.getProfileNum(),
                        roi,
                        totalAsset,
                        info.getUsingAsset(),
                        totalCurr
                ),
                stockList
        );
    }
}

