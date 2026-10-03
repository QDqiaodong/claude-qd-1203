package com.tailor.shop.garment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "garment")
public class Garment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /** 衣物编号（唯一） */
    public String code;

    /** 客户姓名 */
    public String customer;

    /** 品类：西装 / 裙 / 裤 / 大衣 */
    public String category;

    /** 材质 */
    public String material;

    /** 状态：待改 / 修改中 / 待取 / 已取 */
    public String status;

    public LocalDateTime createdAt = LocalDateTime.now();
}
