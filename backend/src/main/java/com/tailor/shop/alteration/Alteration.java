package com.tailor.shop.alteration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "alteration")
public class Alteration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /** 关联衣物 id（必填） */
    public Long garmentId;

    /** 项目：长短 / 宽窄 / 换拉链 / 换扣 */
    public String item;

    /** 报价（必须 > 0） */
    public Double price;

    /** 状态：待开工 / 已完成 */
    public String status;

    public LocalDateTime createdAt = LocalDateTime.now();
}
