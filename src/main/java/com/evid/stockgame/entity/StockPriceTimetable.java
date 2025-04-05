package com.evid.stockgame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "stock_price_timetable")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPriceTimetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_price_timetable_id", nullable = false)
    private Integer stockPriceTimetableId;

    @Column(name = "round_num")
    private Integer roundNum;

    @Column(name = "time")
    private Integer time;

    @Column(name = "stock_price")
    private Integer stockPrice;

    // ✅ 연관관계 매핑: stock_price_timetable.room_id → room.room_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "stock_price_timetable_ibfk_1"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    // ✅ 연관관계 매핑: stock_price_timetable.company_id → company.company_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", foreignKey = @ForeignKey(name = "stock_price_timetable_ibfk_2"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;
}
