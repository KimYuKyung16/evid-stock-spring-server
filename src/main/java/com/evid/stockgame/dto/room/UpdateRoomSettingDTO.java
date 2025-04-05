package com.evid.stockgame.dto.room;

public record UpdateRoomSettingDTO (
    int round_num,
    int time_limit,
    int seed
) {}
