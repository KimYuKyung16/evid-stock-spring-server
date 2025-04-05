package com.evid.stockgame.dto;

import java.util.List;

public record NewsResponseDTO(
        List<String> com_name,
        List<Boolean> isGood,
        List<String> descriptions
) {}