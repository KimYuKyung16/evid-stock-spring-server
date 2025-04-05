package com.evid.stockgame.dto.user;

public record UserInfoDTO(
        String username,
        int profile_num,
        double total_roi,
        double total_asset,
        double using_asset,
        double total_stock_holding
) {
}
