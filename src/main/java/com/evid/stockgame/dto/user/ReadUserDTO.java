package com.evid.stockgame.dto.user;

import com.evid.stockgame.entity.User;

public record ReadUserDTO(
        int user_id,
        String user_name,
        boolean ishost,
        String session_id,
        int profile_num,
        int room_id
) {
    public static ReadUserDTO from(User user) {
        return new ReadUserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getIsHost(),
                user.getSessionId(),
                user.getProfileNum(),
                user.getRoom().getRoomId()
        );
    }
}
