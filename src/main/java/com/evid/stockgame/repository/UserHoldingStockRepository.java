package com.evid.stockgame.repository;

import com.evid.stockgame.entity.Company;
import com.evid.stockgame.entity.UserHoldingStock;
import com.evid.stockgame.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserHoldingStockRepository extends JpaRepository<UserHoldingStock, Integer> {
    List<UserHoldingStock> findByUserInfo(UserInfo userInfo);
    Optional<UserHoldingStock> findByUserInfoAndCompany(UserInfo userInfo, Company company);
}
