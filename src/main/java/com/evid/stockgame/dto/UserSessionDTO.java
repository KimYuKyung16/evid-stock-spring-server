package com.evid.stockgame.dto;

import java.io.Serializable;

public record UserSessionDTO(String userName, int profileNum) implements Serializable {}
