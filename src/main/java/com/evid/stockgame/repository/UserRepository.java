package com.evid.stockgame.repository;

import com.evid.stockgame.entity.Room;
import com.evid.stockgame.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllByRoom(Room room);
    List<User> findByRoom(Room room);
    Optional<User> findBySessionIdAndRoom(String sessionId, Room room);

    @Query("""
        SELECT u FROM User u
        JOIN FETCH u.userInfo ui
        JOIN u.room r
        WHERE u.sessionId = :sessionId AND r.pwd = :pwd
    """)
    Optional<User> findBySessionIdAndRoomPwd(@Param("sessionId") String sessionId, @Param("pwd") String pwd);
}
