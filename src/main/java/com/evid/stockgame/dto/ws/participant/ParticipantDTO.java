package com.evid.stockgame.dto.ws.participant;

import com.evid.stockgame.entity.User;

public record ParticipantDTO(Integer userId, String userName, Integer profileNum, Boolean isHost) {
    public ParticipantDTO(User user) {
        this(user.getUserId(), user.getUserName(), user.getProfileNum(), user.getIsHost());
    }
}
