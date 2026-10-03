package com.tailor.shop.appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /** 关联衣物 id */
    public Long garmentId;

    /** 客户姓名 */
    public String customer;

    /** 预约时间 */
    public LocalDate appointTime;

    /** 取衣时间 */
    public LocalDate pickupTime;

    /** 是否付清 */
    public Boolean paid;

    /** 状态：已预约 / 已取衣 */
    public String status;

    public LocalDateTime createdAt = LocalDateTime.now();
}
