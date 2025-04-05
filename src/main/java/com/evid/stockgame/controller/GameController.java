package com.evid.stockgame.controller;

import com.evid.stockgame.dto.RoundResultDTO;
import com.evid.stockgame.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // 다음 라운드로 이동
    @PutMapping("/{pwd}/next")
    public ResponseEntity<?> proceedToNextRound(@PathVariable String pwd) {
        return ResponseEntity.ok(gameService.advanceToNextRound(pwd));
    }

    // 라운드 결과 저장
    @PostMapping("/{pwd}/result")
    public ResponseEntity<?> saveRoundResult(@PathVariable String pwd) {
        gameService.saveResult(pwd);
        return ResponseEntity.ok().body("성공적으로 저장됨");
    }

    // 게임 결과 조회
    @GetMapping("/{pwd}/result")
    public ResponseEntity<List<RoundResultDTO>> getRoundResults(
            @PathVariable String pwd,
            @RequestParam int round,
            @RequestParam int opt
    ) {
        return ResponseEntity.ok(gameService.getResults(pwd, round, opt));
    }
}
