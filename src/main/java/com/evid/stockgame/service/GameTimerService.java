package com.evid.stockgame.service;

import com.evid.stockgame.entity.Company;
import com.evid.stockgame.entity.Room;
import com.evid.stockgame.entity.StockPrice;
import com.evid.stockgame.entity.StockPriceTimetable;
import com.evid.stockgame.repository.CompanyRepository;
import com.evid.stockgame.repository.RoomRepository;
import com.evid.stockgame.repository.StockPriceRepository;
import com.evid.stockgame.repository.StockPriceTimetableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameTimerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomRepository roomRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StockPriceTimetableRepository stockPriceTimetableRepository;
    private final CompanyRepository companyRepository;

    private final Map<String, ScheduledFuture<?>> activeTimers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public void startTimer(String roomCode) {
        Room room = roomRepository.findByPwd(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

        int timeLimit = room.getTimeLimit();
        int round = room.getCurrRound();
        int roomId = room.getRoomId();

        // ⏱ 최초 주가 그래프 데이터 저장
        insertInitialStockPrices(room);

        // 타이머를 모든 클라이언트에게 시작 신호를 보냄
        long startTime = System.currentTimeMillis();
        messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/timer-started",
                Map.of("startTime", startTime, "duration", timeLimit));

        Runnable timerTask = new Runnable() {
            int elapsed = 0;

            @Override
            public void run() {
                elapsed++;
                int remaining = timeLimit - elapsed;

                messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/timer-tick", remaining);

                if (remaining <= 0) {
                    messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/timer-ended", round);
                    activeTimers.remove(roomCode).cancel(true);
                    return;
                }

                if (remaining != timeLimit && remaining % 30 == 0) {
                    insertPeriodicStockPrices(room, remaining);
                    messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/update-stock-graph", Map.of());
                }
            }
        };

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(timerTask, 1, 1, TimeUnit.SECONDS);
        activeTimers.put(roomCode, future);
    }

    // 타이머를 처음 시작했을 때 stock_price_timetable에 제일 초기값 추가
    private void insertInitialStockPrices(Room room) {
        List<Company> companies = companyRepository.findAll();

        for (Company company : companies) {
            StockPrice stockPrice = stockPriceRepository.findByRoomAndCompany(room, company)
                    .orElseThrow();

            StockPriceTimetable timetable = StockPriceTimetable.builder()
                    .room(room)
                    .company(company)
                    .roundNum(room.getCurrRound())
                    .time(room.getTimeLimit())
                    .stockPrice(stockPrice.getCurrentPrice())
                    .build();

            stockPriceTimetableRepository.save(timetable);
        }
    }

    // 시간이 지남에 따라 stock_price_timetable에 그래프값 추가
    private void insertPeriodicStockPrices(Room room, int timeLeft) {
        List<Company> companies = companyRepository.findAll();

        for (Company company : companies) {
            StockPrice stockPrice = stockPriceRepository.findByRoomAndCompany(room, company)
                    .orElseThrow();

            StockPriceTimetable timetable = StockPriceTimetable.builder()
                    .room(room)
                    .company(company)
                    .roundNum(room.getCurrRound())
                    .time(timeLeft)
                    .stockPrice(stockPrice.getCurrentPrice())
                    .build();

            stockPriceTimetableRepository.save(timetable);
        }
    }
}

