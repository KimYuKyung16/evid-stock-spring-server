package com.evid.stockgame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "user_holding_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHoldingStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_holding_stock_id", nullable = false)
    private Integer userHoldingStockId;

    @Column(name = "buying_price")
    private Integer buyingPrice;

    @Column(name = "holding_stock_num")
    private Integer holdingStockNum;

    // ✅ 연관관계 매핑: user_holding_stock.company_id → company.company_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", foreignKey = @ForeignKey(name = "user_holding_stock_ibfk_1"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    // ✅ 연관관계 매핑: user_holding_stock.user_info_id → user_info.user_info_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_id", foreignKey = @ForeignKey(name = "user_holding_stock_ibfk_2"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserInfo userInfo;

    public void update(int newAvgPrice, int newAmount) {
        this.buyingPrice = newAvgPrice;;
        this.holdingStockNum = newAmount;
    }
}
