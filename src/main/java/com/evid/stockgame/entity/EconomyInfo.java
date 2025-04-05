package com.evid.stockgame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "economy_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EconomyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "economy_info_id", nullable = false)
    private Integer economyInfoId;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "isgood")
    private Boolean isGood;

    // ✅ 연관관계 매핑: economy_info.company_id → company.company_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", foreignKey = @ForeignKey(name = "economy_info_ibfk_1"))
    @OnDelete(action = OnDeleteAction.CASCADE) // 삭제 시 CASCADE 동작
    private Company company;
}
