package com.tailor.shop.fabric;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "fabric")
public class Fabric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /** 面料名称（唯一） */
    public String name;

    /** 类别：里布 / 衬布 / 拉链 / 纽扣 / 花边 / 配线 */
    public String category;

    /** 库存 */
    public Integer stock;

    /** 单位 */
    public String unit;

    /** 安全库存下限 */
    public Integer threshold;

    public LocalDateTime createdAt = LocalDateTime.now();
}
