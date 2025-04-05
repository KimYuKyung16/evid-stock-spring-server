package com.evid.stockgame.controller;

import com.evid.stockgame.dto.user.CreateUserRequest;
import com.evid.stockgame.dto.user.ReadUserDTO;
import com.evid.stockgame.dto.user.ReadUserInfoResponse;
import com.evid.stockgame.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 유저 정보 조회
     */
    @GetMapping("/{pwd}")
    public ResponseEntity<?> getUserInfo(@PathVariable String pwd, HttpSession session) {
        String sessionId = session.getId();
        try {
            ReadUserInfoResponse response = userService.getUserInfo(pwd, sessionId);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("유저 정보 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "데이터베이스 오류"));
        }
    }

    /**
     * 유저 정보 저장
     */
    @PostMapping("/{pwd}")
    public ResponseEntity<?> createUser(
            @PathVariable String pwd,
            @Valid @RequestBody CreateUserRequest request,
            HttpSession session
    ) {
        log.info("test={} {} {}", pwd, request, session);

        userService.createUser(pwd, request, session);
        return ResponseEntity.ok("사용자 및 유저 정보 생성 완료");
    }

    /**
     * 유저 정보 삭제
     */
    @DeleteMapping("/{pwd}")
    public ResponseEntity<?> leaveRoom(@PathVariable String pwd, HttpSession session) {
        String sessionId = session.getId();
        try {
            List<ReadUserDTO> users = userService.deleteUserFromRoom(pwd, sessionId);
            session.invalidate();
            return ResponseEntity.ok(users);
        } catch (IllegalStateException e) {
            session.invalidate();
            return ResponseEntity.ok().build(); // 방이 먼저 삭제된 경우
        } catch (Exception e) {
            log.error("유저 나가기 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "데이터베이스 오류"));
        }
    }

}
