package com.evid.stockgame.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "pwd", length = 255)
    private String pwd;

    @Column(name = "round_num")
    private Integer roundNum;

    @Column(name = "seed")
    private Integer seed;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Column(name = "curr_round")
    private Integer currRound;
}
