package com.evid.stockgame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "stock_price")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_price_id", nullable = false)
    private Integer stockPriceId;

    @Column(name = "before_price")
    private Integer beforePrice;

    @Column(name = "current_price")
    private Integer currentPrice;

    // ✅ 연관관계 매핑: stock_price.company_id → company.company_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", foreignKey = @ForeignKey(name = "stock_price_ibfk_1"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    // ✅ 연관관계 매핑: stock_price.room_id → room.room_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "stock_price_ibfk_2"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;
}
