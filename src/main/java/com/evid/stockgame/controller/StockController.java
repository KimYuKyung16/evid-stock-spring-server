package com.evid.stockgame.controller;

import com.evid.stockgame.dto.*;
import com.evid.stockgame.service.StockService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final StockService stockService;

    // 종목 가져오기 - 테스트 완료
    @GetMapping
    public ResponseEntity<?> getStockList(@RequestParam String pwd) {
        try {
            List<StockInfoResponse> result = stockService.getStockList(pwd);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "서버 오류"));
        }
    }

    // 특정 종목 조회하기
    @GetMapping("/{companyId}")
    public ResponseEntity<?> getStockInfo(@PathVariable int companyId, @RequestParam String pwd) {
        try {
            StockInfoResponse response = stockService.getStockInfo(companyId, pwd);
            log.info("repsonse ={}",response);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "서버 오류"));
        }
    }

    // 특정 종목 그래프 조회하기
    @GetMapping("/{companyId}/graph")
    public ResponseEntity<?> getStockGraph(
            @PathVariable int companyId,
            @RequestParam String pwd
    ) {
        try {
            List<Map<String, Object>> response = stockService.getCompanyStockGraph(companyId, pwd);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "서버 오류"));
        }
    }

    // 매수하기
    @PostMapping("/{id}")
    public ResponseEntity<BuyStockResponse> buyStock(
            @PathVariable int id,
            @RequestBody BuyStockRequest request,
            HttpSession session
    ) {
        String sessionId = session.getId();
        BuyStockResponse response = stockService.buyStock(id, sessionId, request);
        return ResponseEntity.ok(response);
    }

    // 매도하기
    @DeleteMapping("/{id}")
    public ResponseEntity<SellStockResponse> sellStock(
            @PathVariable int id,
            @RequestBody SellStockRequest request,
            HttpSession session
    ) {
        String sessionId = session.getId();
        SellStockResponse response = stockService.sellStock(id, sessionId, request);
        return ResponseEntity.ok(response);
    }
}
