package com.evid.stockgame.repository;


import com.evid.stockgame.entity.Company;
import com.evid.stockgame.entity.Room;
import com.evid.stockgame.entity.StockPrice;
import com.evid.stockgame.entity.StockPriceTimetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockPriceTimetableRepository extends JpaRepository<StockPriceTimetable, Integer> {
//    List<Company> findAll();
    Optional<StockPrice> findByRoomAndCompany(Room room, Company company);
    List<StockPriceTimetable> findByCompanyAndRoomAndRoundNumOrderByTimeDesc(
            Company company, Room room, int roundNum
    );
}
