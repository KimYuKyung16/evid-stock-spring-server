package com.evid.stockgame.controller;

import com.evid.stockgame.dto.ws.game.*;
import com.evid.stockgame.service.GameTimerService;
import com.evid.stockgame.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameTimerService gameTimerService;
    private final StockService stockService;

    // 게임 시작
    @MessageMapping("/game/start") // 클라이언트가 보낼 주소
    public void startGame(GameStartRequest request) {
        log.info("🎮 game start: roomCode={}", request.roomCode());

        GameStartResponse response = new GameStartResponse("start", "게임이 시작되었습니다.");
        // 해당 방의 유저들에게 게임 시작 알림
        messagingTemplate.convertAndSend("/topic/room/participants/" + request.roomCode(), response);
    }

    // 타이머 시작
    @MessageMapping("/game/start-timer")
    public void startTimer(StartTimerRequest request) {
        gameTimerService.startTimer(request.roomCode());
    }

    // 타이머 중지
    @MessageMapping("/game/stop-timer")
    public void stopTimer(StopTimerRequest request) {
        String roomCode = request.roomCode();

        log.info("stop timer: roomCode = {}", roomCode);
        // 클라이언트들에게 타이머 중지 알림 전송
        messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/timer-stopped",
                new StopTimerResponse("타이머가 중지되었습니다."));
    }

    // 주식 그래프 가져오기
    @MessageMapping("/stock/graph")
    public void getStockGraph(@Payload StockGraphRequest request) {
        stockService.sendStockGraph(request);
    }

    // 게임 종료
    @MessageMapping("/game/end")
    public void gameEnded(@Payload GameEndRequest request) {
        String roomCode = request.roomCode();
        messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/ended",
                Map.of("message", "게임이 종료되었습니다."));
    }
}
