package com.evid.stockgame.repository;

import com.evid.stockgame.entity.Room;
import com.evid.stockgame.entity.RoundResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundResultRepository extends JpaRepository<RoundResult, Integer> {
    List<RoundResult> findByRoomAndRoundResultRound(Room room, int round);
}

