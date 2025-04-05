package com.evid.stockgame.dto;


import com.evid.stockgame.entity.Company;

//@Schema(description = "상품 정보를 반환하기 위한 DTO")
public record ReadCompanyDTO(
        Integer companyId,
        String companyName
) {
    // Company 엔티티를 받아서 DTO로 변환하는 생성자
    public ReadCompanyDTO(Company company) {
        this(
                company.getCompanyId(),
                company.getCompanyName()
        );
    }
}
