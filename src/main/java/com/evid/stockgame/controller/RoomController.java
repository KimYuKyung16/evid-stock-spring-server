package com.evid.stockgame.controller;

import com.evid.stockgame.dto.room.CreateRoomResponse;
import com.evid.stockgame.dto.room.UpdateRoomSettingDTO;
import com.evid.stockgame.entity.Room;
import com.evid.stockgame.repository.RoomRepository;
import com.evid.stockgame.service.RoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomRepository roomRepository;

    // 방 생성
    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(HttpSession session) {
        log.info("session: {}", session);
        return ResponseEntity.ok(roomService.createRoomWithHost(session.getId()));
    }

    // 방 정보 변경
    @PutMapping("/{pwd}")
    public ResponseEntity<?> updateRoomInfo(@PathVariable String pwd, @RequestBody UpdateRoomSettingDTO dto) {
        roomService.updateRoomSettings(pwd, dto);
        return ResponseEntity.ok().body(Map.of("message", "방 정보가 업데이트되었습니다."));
    }

    // 방 정보 조회
    @GetMapping("/{pwd}/setting")
    public ResponseEntity<?> getRoomSetting(@PathVariable String pwd) {
        Optional<Room> roomOpt = roomRepository.findByPwd(pwd);
        return roomOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "방을 찾을 수 없습니다.")));
    }

}
