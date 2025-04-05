package com.evid.stockgame.controller;

import com.evid.stockgame.dto.RoundInfoResponse;
import com.evid.stockgame.dto.ws.game.GetRoundRequest;
import com.evid.stockgame.dto.ws.participant.ParticipantResponse;
import com.evid.stockgame.dto.ws.room.RoomConnectRequest;
import com.evid.stockgame.dto.ws.room.RoomConnectResponse;
import com.evid.stockgame.dto.ws.room.RoomDeleteRequest;
import com.evid.stockgame.dto.ws.room.RoomLeaveRequest;
import com.evid.stockgame.entity.Room;
import com.evid.stockgame.repository.RoomRepository;
import com.evid.stockgame.service.ParticipantService;
import com.evid.stockgame.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomSocketController {

    private final ParticipantService participantService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final RoomRepository roomRepository;

    // 방 접속
    @MessageMapping("/room/connect")
    public void connectToRoom(RoomConnectRequest request) {
        String roomCode = request.roomCode();
        log.info("room connect: roomCode={}", roomCode);
        RoomConnectResponse response = new RoomConnectResponse("connectComplete", roomCode);
        messagingTemplate.convertAndSend("/topic/room/connect/complete/" + roomCode, response);
    }

    // 참여자 목록 요청 이벤트 처리
    @MessageMapping("/room/participants")
    public void getParticipants(RoomConnectRequest request) {
        log.info("👥 participants request: roomCode={}", request);

        ParticipantResponse response = participantService.getParticipants(request.roomCode());
        log.info("response:{}", response);
        messagingTemplate.convertAndSend("/topic/room/participants/" + request.roomCode(), response);
    }

    // 방 나가기
    @MessageMapping("/room/leave")
    public void leaveRoom(RoomLeaveRequest request,
                          StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        roomService.removeUserFromRoom(request.roomCode(), sessionId);
    }

    // 방 제거
    @MessageMapping("/room/delete")
    public void deleteRoom(RoomDeleteRequest request) {
        roomService.deleteRoom(request.roomCode());
    }

    // 라운드 정보 가져오기
    @MessageMapping("/round/get")
    public void getRound(GetRoundRequest request) {
        String roomCode = request.roomCode();

        Room room = roomRepository.findByPwd(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없음"));

        RoundInfoResponse response = new RoundInfoResponse(
                room.getCurrRound(),
                room.getRoundNum()
        );

        messagingTemplate.convertAndSend("/topic/round/" + roomCode + "/notify", response);
    }

}
