package com.evid.stockgame.service;

import com.evid.stockgame.dto.ws.participant.ParticipantDTO;
import com.evid.stockgame.dto.ws.participant.ParticipantResponse;
import com.evid.stockgame.entity.Room;
import com.evid.stockgame.repository.RoomRepository;
import com.evid.stockgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ParticipantResponse getParticipants(String roomCode) {
        log.info("roomCode={}", roomCode);
        Room room = roomRepository.findByPwd(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없음"));

        List<ParticipantDTO> participants = userRepository.findAllByRoom(room)
                .stream()
                .map(ParticipantDTO::new)
                .toList();

        return new ParticipantResponse(participants);
    }
}
