package com.evid.stockgame.service;

import com.evid.stockgame.dto.UserSessionDTO;
import com.evid.stockgame.dto.user.CreateUserRequest;
import com.evid.stockgame.dto.user.ReadUserDTO;
import com.evid.stockgame.dto.user.ReadUserInfoResponse;
import com.evid.stockgame.entity.*;
import com.evid.stockgame.exception.RoomNotFoundException;
import com.evid.stockgame.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final StockPriceRepository stockPriceRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserHoldingStockRepository userHoldingStockRepository;
    private final CompanyRepository companyRepository;

    // 유저 생성
    @Transactional
    @PostMapping("/{pwd}")
    public void createUser(String pwd, CreateUserRequest request,  HttpSession session) {
        // 1. 방 찾기
        Room room = roomRepository.findByPwd(pwd)
                .orElseThrow(() -> new RoomNotFoundException("방을 찾을 수 없습니다"));

        // 2. 유저 생성
        User user = User.builder()
                .userName(request.userName())
                .profileNum(request.profileNum())
                .room(room)
                .sessionId(session.getId())
                .isHost(false)
                .build();
        userRepository.save(user);

       // 3. 유저 정보 저장
        UserInfo userInfo = UserInfo.builder()
                .totalRoi(0)
                .user(user)
                .totalAsset(room.getSeed())
                .usingAsset(room.getSeed())
                .totalStockHolding(0)
                .build();
        userInfoRepository.save(userInfo);

        session.setAttribute("user", new UserSessionDTO(user.getUserName(), user.getProfileNum()));
    }

    // 유저 조회
    @Transactional(readOnly = true)
    public ReadUserInfoResponse getUserInfo(String pwd, String sessionId) {
        Room room = roomRepository.findByPwd(pwd)
                .orElseThrow(() -> new IllegalStateException("방을 찾을 수 없음"));

        User user = userRepository.findBySessionIdAndRoom(sessionId, room)
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없음"));

        UserInfo userInfo = userInfoRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("유저 정보 없음"));

        List<Company> companies = companyRepository.findAll();
        Map<Integer, String> companyNames = companies.stream()
                .collect(Collectors.toMap(Company::getCompanyId, Company::getCompanyName));

        List<StockPrice> stockPrices = stockPriceRepository.findByRoom(room);
        Map<Integer, StockPrice> priceMap = stockPrices.stream()
                .collect(Collectors.toMap(sp -> sp.getCompany().getCompanyId(), sp -> sp));

        List<UserHoldingStock> holdings = userHoldingStockRepository.findByUserInfo(userInfo);

        return ReadUserInfoResponse.from(user, userInfo, holdings, companyNames, priceMap);
    }

    // 유저 삭제
    @Transactional
    public List<ReadUserDTO> deleteUserFromRoom(String pwd, String sessionId) {
        Room room = roomRepository.findByPwd(pwd)
                .orElseThrow(() -> new IllegalStateException("방을 찾을 수 없음"));

        Optional<User> userOpt = userRepository.findBySessionIdAndRoom(sessionId, room);

        if (userOpt.isEmpty()) {
            throw new IllegalStateException("유저 없음"); // 방은 있는데 유저가 없음 (방 삭제 직전 상태)
        }

        userRepository.delete(userOpt.get());

        // 남은 유저 목록
        List<User> users = userRepository.findByRoom(room);

        return users.stream()
                .map(ReadUserDTO::from)
                .toList();
    }
}
