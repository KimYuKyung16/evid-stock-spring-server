package com.evid.stockgame.controller;

import com.evid.stockgame.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
//@Slf4j
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;


}
