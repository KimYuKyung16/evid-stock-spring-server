package com.evid.stockgame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_info_id", nullable = false)
    private Integer userInfoId;

    @Column(name = "total_roi", nullable = false)
    private Integer totalRoi;

    @Column(name = "total_asset", nullable = false)
    private Integer totalAsset;

    @Column(name = "using_asset", nullable = false)
    private Integer usingAsset;

    @Column(name = "total_stock_holding", nullable = false)
    private Integer totalStockHolding;

    // ✅ user_id 외래 키 → User 엔티티 연관관계로 매핑
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_info_ibfk_1"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    // ✅ 커스텀 메서드 추가: 자산 업데이트용
    public void updateAsset(int newUsingAsset, int newTotalStockHolding) {
        this.usingAsset = newUsingAsset;
        this.totalStockHolding = newTotalStockHolding;
        this.totalAsset = newUsingAsset + newTotalStockHolding;
    }
}
