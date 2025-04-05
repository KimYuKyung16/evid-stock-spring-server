package com.evid.stockgame.service;

import com.evid.stockgame.dto.ReadCompanyDTO;
import com.evid.stockgame.entity.Company;
import com.evid.stockgame.repository.CompanyRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<Company> getAllCompanys(Integer storeId) {
        List<Company> companys = companyRepository.findAll();
        return companys;
//        return companys.stream()
//                .map(ReadCompanyDTO::new)
//                .collect(Collectors.toList());
    }

}
