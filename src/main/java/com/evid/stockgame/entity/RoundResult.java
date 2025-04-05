package com.evid.stockgame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "round_result")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoundResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_result_id", nullable = false)
    private Integer roundResultId;

    @Column(name = "round_result_name", length = 255)
    private String roundResultName;

    @Column(name = "round_result_total_price")
    private Integer roundResultTotalPrice;

    @Column(name = "round_result_roi")
    private Integer roundResultRoi;

    @Column(name = "round_result_round")
    private Integer roundResultRound;

    @Column(name = "round_result_profile")
    private Integer roundResultProfile;

    // ✅ 연관관계 매핑: round_result.room_id → room.room_id (CASCADE on update/delete)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "round_result_ibfk_1"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;
}
