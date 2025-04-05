package com.evid.stockgame.repository;

import com.evid.stockgame.entity.EconomyInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface EconomyInfoRepository extends JpaRepository<EconomyInfo, Integer> {

    @Query(value = "SELECT * FROM economy_info WHERE company_id = :companyId ORDER BY RAND() LIMIT 1", nativeQuery = true)
    EconomyInfo findRandomByCompanyId(@Param("companyId") int companyId);

}
