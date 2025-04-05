package com.evid.stockgame.repository;

import com.evid.stockgame.entity.User;
import com.evid.stockgame.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByUser(User user);
}
