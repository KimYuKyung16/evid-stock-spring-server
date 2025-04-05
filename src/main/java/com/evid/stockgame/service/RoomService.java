package com.evid.stockgame.service;

import com.evid.stockgame.dto.room.CreateRoomResponse;
import com.evid.stockgame.dto.room.UpdateRoomSettingDTO;
import com.evid.stockgame.dto.user.ReadUserDTO;
import com.evid.stockgame.entity.*;
import com.evid.stockgame.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomNewsRepository roomNewsRepository;
    private final EconomyInfoRepository economyInfoRepository;
    private final StockPriceRepository stockPriceRepository;
    private final UserInfoRepository userInfoRepository;
    private final CompanyRepository companyRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 100;

    @Transactional
    public CreateRoomResponse createRoomWithHost(String sessionId) {
        String roomCode = generateUniqueCode();

        // 1. 방 생성
        Room room = Room.builder()
                .pwd(roomCode)
                .roundNum(10)
                .timeLimit(60)
                .seed(5_000_000)
                .currRound(1)
                .build();
        roomRepository.save(room);

        // 2. 호스트 유저 생성
        User host = User.builder()
                .userName("HOST")
                .profileNum(0)
                .room(room)
                .sessionId(sessionId)
                .isHost(true)
                .build();
        userRepository.save(host);

        // 3. 뉴스 10개 생성 (회사별 1개 랜덤)
        for (int companyId = 1; companyId <= 10; companyId++) {
            EconomyInfo info = economyInfoRepository.findRandomByCompanyId(companyId);
            if (info == null) {
                log.warn("회사 {}에 대한 뉴스 없음", companyId);
                continue;
            }
            RoomNews news = RoomNews.builder()
                    .room(room)
                    .economyInfo(info)
                    .build();
            roomNewsRepository.save(news);
        }

        return new CreateRoomResponse(roomCode);
    }

    // 랜덤 방코드 생성 - 방코드 중복이 아닌 동안
    private String generateUniqueCode() {
        int attempts = 0;
        String code;
        do {
            code = generateCode();
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                throw new IllegalStateException("방 코드 생성 실패");
            }
        } while (roomRepository.findByPwd(code).isPresent()); // 중복 검사
        return code;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }


    @Transactional
    public void updateRoomSettings(String pwd, UpdateRoomSettingDTO dto) {
        Room room = roomRepository.findByPwd(pwd)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

        // 1. 방 정보 업데이트
        room.setRoundNum(dto.round_num());
        room.setTimeLimit(dto.time_limit());
        room.setSeed(dto.seed());
        roomRepository.save(room);

        // 2. 회사별 주가 생성
        for (int i = 1; i <= 10; i++) {
            int stockPrice = dto.seed() > 500
                    ? (int) (Math.random() * 150001 + 50000)
                    : (int) (Math.random() * 45001 + 5000);

            final int companyId = i;
            Company company = companyRepository.findById(i)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회사가 존재하지 않습니다: companyId=" + companyId));

            StockPrice sp = new StockPrice();
            sp.setBeforePrice(stockPrice);
            sp.setCurrentPrice(stockPrice);
            sp.setCompany(company);
            sp.setRoom(room);
            stockPriceRepository.save(sp);
        }

        // 3. 해당 방 유저 정보 불러와서 자산 설정
        List<User> users = userRepository.findByRoom(room);
        for (User user : users) {
            if (user.getIsHost()) continue; // host는 자산X
            UserInfo userInfo = userInfoRepository.findByUser(user)
                    .orElseThrow(() -> new IllegalStateException("user_info 없음"));
            userInfo.setTotalAsset(dto.seed());
            userInfo.setUsingAsset(dto.seed());
            userInfoRepository.save(userInfo);
        }

        log.info("✅ 방 정보 및 주가, 유저 자산 업데이트 완료: roomCode={}", pwd);
    }

    public void removeUserFromRoom(String roomCode, String sessionId) {
        Room room = roomRepository.findByPwd(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없음"));

        Optional<User> userOpt = userRepository.findBySessionIdAndRoom(sessionId, room);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            log.info("사용자 삭제 완료 - 세션: {}, 방: {}", sessionId, roomCode);
        }

        // 모든 참여자에게 참여자 목록 갱신 전송
        List<User> updatedUsers = userRepository.findByRoom(room);
        List<ReadUserDTO> response = updatedUsers.stream()
                .map(ReadUserDTO::from)
                .toList();

        messagingTemplate.convertAndSend("/sub/room/" + roomCode + "/participants", response);
    }

    public void deleteRoom(String roomCode) {
        Room room = roomRepository.findByPwd(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없음"));

        // 모든 유저 삭제 (FK CASCADE 설정돼 있으면 생략 가능)
        List<User> users = userRepository.findByRoom(room);
        userRepository.deleteAll(users);

        // 방 삭제
        roomRepository.delete(room);
        log.info("방 삭제 완료: {}", roomCode);

        // 클라이언트에 알림 전송
        messagingTemplate.convertAndSend("/sub/room/" + roomCode + "/delete",
                Map.of("message", "방이 삭제되었습니다."));
    }
}
