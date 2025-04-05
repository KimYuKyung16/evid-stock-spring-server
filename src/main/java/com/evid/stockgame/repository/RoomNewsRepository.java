package com.evid.stockgame.repository;

import com.evid.stockgame.entity.Room;
import com.evid.stockgame.entity.RoomNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomNewsRepository extends JpaRepository<RoomNews, Integer> {
    @Query("""
    SELECT rn FROM RoomNews rn
    JOIN FETCH rn.economyInfo
    WHERE rn.room.pwd = :pwd
""")
    List<RoomNews> findAllWithEconomyByRoomPwd(@Param("pwd") String pwd);

    @Query("""
    SELECT rn FROM RoomNews rn
    JOIN FETCH rn.economyInfo
    WHERE rn.room = :room
""")
    List<RoomNews> findAllWithEconomyByRoom(@Param("room") Room room);

    List<RoomNews> findByRoom(Room room);
}
