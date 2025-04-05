package com.evid.stockgame.repository;

import com.evid.stockgame.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//Company: 이 리포지토리는 Company 엔티티를 다룸
//Integer: Product 엔티티의 기본 키(PK) 타입이 Integer임
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findById(int id);
}
