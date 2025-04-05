package com.evid.stockgame.service;

import com.evid.stockgame.dto.NewsResponseDTO;
import com.evid.stockgame.entity.Company;
import com.evid.stockgame.entity.Room;
import com.evid.stockgame.entity.RoomNews;
import com.evid.stockgame.repository.CompanyRepository;
import com.evid.stockgame.repository.RoomNewsRepository;
import com.evid.stockgame.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final RoomRepository roomRepository;
    private final CompanyRepository companyRepository;
    private final RoomNewsRepository roomNewsRepository;

    public NewsResponseDTO getNewsByRoomPwd(String pwd) {
        Room room = roomRepository.findByPwd(pwd)
                .orElseThrow(() -> new IllegalStateException("방을 찾을 수 없습니다."));

        // 회사 이름 목록 조회
        List<String> comNames = companyRepository.findAll().stream()
                .sorted(Comparator.comparing(Company::getCompanyId)) // 1~10 순서 정렬 보장
                .map(Company::getCompanyName)
                .toList();

        // room_news + economy_info JOIN 결과 조회
        List<RoomNews> newsList = roomNewsRepository.findAllWithEconomyByRoom(room);

        List<Boolean> isGoodList = new ArrayList<>();
        List<String> descriptionList = new ArrayList<>();

        for (RoomNews rn : newsList) {
            isGoodList.add(rn.getEconomyInfo().getIsGood());
            descriptionList.add(rn.getEconomyInfo().getDescription());
        }

        return new NewsResponseDTO(comNames, isGoodList, descriptionList);
    }
}
