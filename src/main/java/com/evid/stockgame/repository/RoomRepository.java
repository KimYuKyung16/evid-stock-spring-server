package com.evid.stockgame.repository;

import com.evid.stockgame.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    Optional<Room> findByPwd(String pwd);
}
