package com.evid.stockgame.dto.user;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "유저 이름은 필수입니다.") String userName,
        Integer profileNum
) {
}
