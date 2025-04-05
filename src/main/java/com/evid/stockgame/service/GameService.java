package com.evid.stockgame.service;

import com.evid.stockgame.dto.RoundAdvanceResponse;
import com.evid.stockgame.dto.RoundResultDTO;
import com.evid.stockgame.entity.*;
import com.evid.stockgame.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final RoomRepository roomRepository;
    private final RoomNewsRepository roomNewsRepository;
    private final EconomyInfoRepository economyInfoRepository;
    private final StockPriceRepository stockPriceRepository;
    private final UserRepository userRepository;
    private final UserHoldingStockRepository holdingStockRepository;
    private final RoundResultRepository roundResultRepository;

    private static final Random random = new Random();

    @Transactional
    public RoundAdvanceResponse advanceToNextRound(String pwd) {
        Room room = roomRepository.findByPwd(pwd).orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없음"));

        int currRound = room.getCurrRound() + 1;
        if (currRound > room.getRoundNum()) {
            return new RoundAdvanceResponse("end", "라운드 종료");
        }

        // 주가 정보 가져오기
        List<StockPrice> prices = stockPriceRepository.findByRoom(room);
        Map<Integer, StockPrice> priceMap = new HashMap<>();
        for (StockPrice sp : prices) {
            priceMap.put(sp.getCompany().getCompanyId(), sp);
        }

        // 뉴스 갱신
        List<RoomNews> roomNewsList = roomNewsRepository.findByRoom(room);
        for (RoomNews rn : roomNewsList) {
            int companyId = rn.getEconomyInfo().getCompany().getCompanyId();
            EconomyInfo randomNews = economyInfoRepository.findRandomByCompanyId(companyId);
            rn.setEconomyInfo(randomNews);
        }

        // 라운드 업데이트
        room.setCurrRound(currRound);

        // 주가 갱신
        for (RoomNews rn : roomNewsList) {
            EconomyInfo news = rn.getEconomyInfo();
            int companyId = news.getCompany().getCompanyId();
            StockPrice sp = priceMap.get(companyId);

            int beforePrice = sp.getCurrentPrice();
            int ran = random.nextInt(25) + 1;
            int newPrice = news.getIsGood() ?
                    beforePrice + (beforePrice * ran / 100) :
                    beforePrice - (beforePrice * ran / 100);

            sp.setBeforePrice(beforePrice);
            sp.setCurrentPrice(newPrice);
        }

        return new RoundAdvanceResponse("next", "다음 라운드로 성공적으로 수정");
    }

    @Transactional
    public void saveResult(String pwd) {
        Room room = roomRepository.findByPwd(pwd).orElseThrow();
        int round = room.getCurrRound();

        // 회사별 주가 정보
        List<StockPrice> stockPrices = stockPriceRepository.findByRoom(room);
        var companyPrices = stockPrices.stream().collect(
                java.util.stream.Collectors.toMap(
                        sp -> sp.getCompany().getCompanyId(),
                        sp -> sp.getCurrentPrice()
                )
        );

        List<User> users = userRepository.findByRoom(room);
        for (User user : users) {
            if (user.getIsHost()) continue;
            UserInfo info = user.getUserInfo();
            List<UserHoldingStock> holdings = holdingStockRepository.findByUserInfo(info);

            int totalCurr = 0;
            int totalBuy = 0;
            for (UserHoldingStock holding : holdings) {
                int comId = holding.getCompany().getCompanyId();
                int currentPrice = companyPrices.get(comId);
                int buyPrice = holding.getBuyingPrice();
                int count = holding.getHoldingStockNum();
                totalCurr += currentPrice * count;
                totalBuy += buyPrice * count;
            }

            double roi = (totalBuy == 0) ? 0 : ((double)(totalCurr - totalBuy) / totalBuy) * 100;
            int totalAsset = totalCurr + info.getUsingAsset();

            RoundResult result = RoundResult.builder()
                    .roundResultName(user.getUserName())
                    .roundResultTotalPrice(totalAsset)
                    .roundResultRoi((int) Math.round(roi))
                    .roundResultRound(round)
                    .room(room)
                    .roundResultProfile(user.getProfileNum())
                    .build();

            roundResultRepository.save(result);
        }
    }

    @Transactional(readOnly = true)
    public List<RoundResultDTO> getResults(String pwd, int round, int opt) {
        Room room = roomRepository.findByPwd(pwd).orElseThrow();
        List<RoundResult> results = roundResultRepository.findByRoomAndRoundResultRound(room, round);

        Comparator<RoundResult> comparator = (opt == 0)
                ? Comparator.comparingInt(RoundResult::getRoundResultTotalPrice).reversed()
                : Comparator.comparingInt(RoundResult::getRoundResultRoi).reversed();

        return results.stream()
                .sorted(comparator)
                .map(r -> new RoundResultDTO(
                        r.getRoundResultName(),
                        r.getRoundResultProfile(),
                        r.getRoundResultTotalPrice(),
                        r.getRoundResultRoi(),
                        0 // 순위는 클라이언트 또는 추가 로직에서 계산 가능
                ))
                .toList();
    }
}
