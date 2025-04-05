package com.evid.stockgame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "user_name", nullable = false, length = 255)
    private String userName;

    @Column(name = "ishost")
    private Boolean isHost;

    @Column(name = "profile_num")
    private Integer profileNum;

    @Column(name = "session_id", length = 255)
    private String sessionId;

    // ✅ 연관관계 매핑: user.room_id → room.room_id
    @ManyToOne
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "user_ibfk_1"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserInfo userInfo;
}
