package com.evid.stockgame.service;

import com.evid.stockgame.dto.*;
import com.evid.stockgame.dto.ws.game.StockGraphRequest;
import com.evid.stockgame.entity.*;
import com.evid.stockgame.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final RoomRepository roomRepository;
    private final StockPriceTimetableRepository stockPriceTimetableRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CompanyRepository companyRepository;
    private final StockPriceRepository stockPriceRepository;
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final UserHoldingStockRepository holdingStockRepository;

    public void sendStockGraph(StockGraphRequest request) {
        String roomCode = request.roomCode();
        int companyId = request.companyId();

        Room room = roomRepository.findByPwd(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없음"));

        int round = room.getCurrRound();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회사를 찾을 수 없음"));

        List<StockPriceTimetable> prices = stockPriceTimetableRepository
                .findByCompanyAndRoomAndRoundNumOrderByTimeDesc(company, room, round);

        messagingTemplate.convertAndSend("/topic/stock/" + roomCode + "/graph", prices);
    }

    public List<StockInfoResponse> getStockList(String pwd) {
        Room room = roomRepository.findByPwd(pwd)
                .orElseThrow(() -> new IllegalStateException("방을 찾을 수 없습니다."));

        List<StockInfoResponse> result = new ArrayList<>();

        for (int companyId = 1; companyId <= 10; companyId++) {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new IllegalStateException("회사 정보를 찾을 수 없습니다."));

            StockPrice stock = stockPriceRepository.findByRoomAndCompany(room, company)
                    .orElseThrow(() -> new IllegalStateException("주가 정보를 찾을 수 없습니다."));

            int curr = (int) stock.getCurrentPrice();
            int prev = (int) stock.getBeforePrice();
            int diff = curr - prev;
            int percent = prev == 0 ? 0 : Math.round(((float) diff / prev) * 100);

            result.add(new StockInfoResponse(
                    curr,
                    prev,
                    diff,
                    percent,
                    company.getCompanyName()
            ));
        }

        return result;
    }

    public StockInfoResponse getStockInfo(int companyId, String pwd) {
        Room room = roomRepository.findByPwd(pwd)
                .orElseThrow(() -> new IllegalStateException("방을 찾을 수 없습니다."));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalStateException("회사 정보를 찾을 수 없습니다."));

        StockPrice stock = stockPriceRepository.findByRoomAndCompany(room, company)
                .orElseThrow(() -> new IllegalStateException("주가 정보를 찾을 수 없습니다."));

        int curr = (int) stock.getCurrentPrice();
        int prev = (int) stock.getBeforePrice();
        int diff = curr - prev;
        int percent = prev == 0 ? 0 : Math.round(((float) diff / prev) * 100);

        return new StockInfoResponse(curr, prev, diff, percent, company.getCompanyName());
    }

    public List<Map<String, Object>> getCompanyStockGraph(int companyId, String pwd) {
        Room room = roomRepository.findByPwd(pwd)
                .orElseThrow(() -> new IllegalStateException("방을 찾을 수 없습니다."));

        int currRound = room.getCurrRound();
        int roomId = room.getRoomId();

        String query = """
            SELECT stock_price
            FROM stock_price_timetable
            WHERE company_id = ? AND room_id = ? AND round_num = ?
            ORDER BY time DESC
        """;

        return jdbcTemplate.queryForList(query, companyId, roomId, currRound);
    }

    public BuyStockResponse buyStock(int companyId, String sessionId, BuyStockRequest request) {
        String pwd = request.pwd();
        int purchaseNum = request.purchase_num();
        double INFLUENCE = 0.002;

        // 1. 유저, 유저정보, 방 조회
        User user = userRepository.findBySessionIdAndRoomPwd(sessionId, pwd)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Room room = user.getRoom();
        UserInfo userInfo = user.getUserInfo();

        // 2. 회사 + 현재 주가 정보 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

        StockPrice stockPrice = stockPriceRepository.findByRoomAndCompany(room, company)
                .orElseThrow(() -> new IllegalArgumentException("주가 정보를 찾을 수 없습니다."));

        int buyingPrice = (int) stockPrice.getCurrentPrice();

        // 3. 주가 반영해서 상승
        int newPrice = (int) (buyingPrice + buyingPrice * purchaseNum * INFLUENCE);
        stockPrice.setCurrentPrice(newPrice);
        stockPriceRepository.save(stockPrice);

        // 4. 기존 보유 주식 조회
        Optional<UserHoldingStock> optionalStock = holdingStockRepository.findByUserInfoAndCompany(userInfo, company);

        int totalCost = buyingPrice * purchaseNum;

        if (optionalStock.isPresent()) {
            UserHoldingStock holding = optionalStock.get();

            int prevAmount = holding.getHoldingStockNum();
            double prevPrice = holding.getBuyingPrice();
            int newAmount = prevAmount + purchaseNum;

            int avgPrice = (int) ((prevPrice * prevAmount + buyingPrice * purchaseNum) / newAmount);

            holding.update(avgPrice, newAmount);
            holdingStockRepository.save(holding);
        } else {
            UserHoldingStock newStock = UserHoldingStock.builder()
                    .userInfo(userInfo)
                    .company(company)
                    .buyingPrice(buyingPrice)
                    .holdingStockNum(purchaseNum)
                    .build();

            holdingStockRepository.save(newStock);
        }

        // 5. user_info 자산 정보 업데이트
        userInfo.updateAsset(
                userInfo.getUsingAsset() - totalCost,
                userInfo.getTotalStockHolding() + totalCost
        );

        // 6. WebSocket 알림
        messagingTemplate.convertAndSend("/sub/room/" + pwd + "/update-stock-list", Map.of());

        return new BuyStockResponse(buyingPrice, buyingPrice + "가격으로 주식 매수 성공");
    }

    @Transactional
    public SellStockResponse sellStock(int companyId, String sessionId, SellStockRequest request) {
        String pwd = request.pwd();
        int sellNum = request.sell_num();
        double INFLUENCE = 0.002;

        // 1. 유저, 유저정보, 방 조회
        User user = userRepository.findBySessionIdAndRoomPwd(sessionId, pwd)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Room room = user.getRoom();
        UserInfo userInfo = user.getUserInfo();

        // 2. 회사 + 현재 주가 정보 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

        StockPrice stockPrice = stockPriceRepository.findByRoomAndCompany(room, company)
                .orElseThrow(() -> new IllegalArgumentException("주가 정보를 찾을 수 없습니다."));

        int currentPrice = (int) stockPrice.getCurrentPrice();

        // 3. 주가 반영해서 하락
        int newPrice = (int) (currentPrice - currentPrice * sellNum * INFLUENCE);
        stockPrice.setCurrentPrice(newPrice);
        stockPriceRepository.save(stockPrice);

        // 4. 기존 보유 주식 조회
        UserHoldingStock holdingStock = holdingStockRepository.findByUserInfoAndCompany(userInfo, company)
                .orElseThrow(() -> new IllegalStateException("보유한 주식이 없습니다."));

        int prevAmount = holdingStock.getHoldingStockNum();
        int remaining = prevAmount - sellNum;

        int profit = currentPrice * sellNum;

        if (remaining > 0) {
            holdingStock.setHoldingStockNum(remaining);
            holdingStockRepository.save(holdingStock);
        } else {
            holdingStockRepository.delete(holdingStock);
        }

        int newUsingAsset = userInfo.getUsingAsset() + profit;
        int stockValueDecrease = currentPrice * sellNum;
        int newTotalStockHolding = userInfo.getTotalStockHolding() - stockValueDecrease;
        userInfo.updateAsset(newUsingAsset, newTotalStockHolding);

        messagingTemplate.convertAndSend("/sub/room/" + pwd + "/update-stock-list", Map.of());

        return new SellStockResponse(currentPrice, currentPrice + "가격으로 주식 매도 성공");
    }
}
