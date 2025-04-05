package com.evid.stockgame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "room_news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ✅ room_id → room.room_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "room_news_ibfk_1"))
    @OnDelete(action = OnDeleteAction.CASCADE) // 삭제 시 CASCADE 동작
    private Room room;

    // ✅ economy_info_id → economy_info.economy_info_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "economy_info_id", foreignKey = @ForeignKey(name = "room_news_ibfk_2"))
    @OnDelete(action = OnDeleteAction.CASCADE) // 삭제 시 CASCADE 동작
    private EconomyInfo economyInfo;
}
