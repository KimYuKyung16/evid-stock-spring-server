package com.evid.stockgame.repository;

import com.evid.stockgame.entity.Company;
import com.evid.stockgame.entity.Room;
import com.evid.stockgame.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockPriceRepository extends JpaRepository<StockPrice, Integer> {
    List<StockPrice> findByRoom(Room room);
    // Optional은 값이 존재할 수도 있고, 존재하지 않을 수도 있는 경우를 안전하게 처리하기 위해 사용하는 컨테이너 클래스
    Optional<StockPrice> findByRoomAndCompany(Room room, Company company);
}
