SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- ---------------------------------------------------------------------
-- 裁缝改衣店 · 衣物台账 / 改衣工单 / 面料库存 / 预约取衣
-- 库名 tailor_shop；application.yml 里 ddl-auto: none，表结构全部由本文件在 MySQL 镜像构建期导入。
-- 注意：MySQL 日期算术一律用 DATE_SUB(CURDATE(), INTERVAL N DAY)，绝不用 CURDATE() - N。
-- 时间戳写 NOW()；预约取衣日期写 DATE_SUB / DATE_ADD。

CREATE DATABASE IF NOT EXISTS tailor_shop
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE tailor_shop;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS alteration;
DROP TABLE IF EXISTS fabric;
DROP TABLE IF EXISTS garment;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------- 衣物台账（主） ----------------
CREATE TABLE garment (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(32) NOT NULL UNIQUE COMMENT '衣物编号',
    customer     VARCHAR(64) NOT NULL COMMENT '客户',
    category     VARCHAR(16) COMMENT '西装 / 裙 / 裤 / 大衣',
    material     VARCHAR(32) COMMENT '材质',
    status       VARCHAR(16) COMMENT '待改 / 修改中 / 待取 / 已取',
    created_at   DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 改衣工单（从：依赖衣物） ----------------
CREATE TABLE alteration (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    garment_id   BIGINT NOT NULL COMMENT '关联衣物 id',
    item         VARCHAR(16) COMMENT '长短 / 宽窄 / 换拉链 / 换扣',
    price        DOUBLE NOT NULL COMMENT '报价',
    status       VARCHAR(16) COMMENT '待开工 / 已完成',
    created_at   DATETIME COMMENT '创建时间',
    CONSTRAINT fk_al_garment FOREIGN KEY (garment_id) REFERENCES garment (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 面料库存 ----------------
CREATE TABLE fabric (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(128) NOT NULL UNIQUE COMMENT '面料名',
    category     VARCHAR(16) COMMENT '里布 / 衬布 / 拉链 / 纽扣 / 花边 / 配线',
    stock        INT NOT NULL COMMENT '库存',
    unit         VARCHAR(16) COMMENT '单位',
    threshold    INT NOT NULL COMMENT '安全库存下限',
    created_at   DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 预约与取衣（从：依赖衣物） ----------------
CREATE TABLE appointment (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    garment_id   BIGINT NOT NULL COMMENT '关联衣物 id',
    customer     VARCHAR(64) COMMENT '客户',
    appoint_time DATE COMMENT '预约时间',
    pickup_time  DATE COMMENT '取衣时间',
    paid         BOOLEAN COMMENT '是否付清',
    status       VARCHAR(16) COMMENT '已预约 / 已取衣',
    created_at   DATETIME COMMENT '创建时间',
    CONSTRAINT fk_ap_garment FOREIGN KEY (garment_id) REFERENCES garment (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 种子数据（含若干异常态，用于真实触发拒绝分支）
-- ---------------------------------------------------------------------

INSERT IGNORE INTO garment (code, customer, category, material, status, created_at) VALUES
('GX-1001', '林小满', '西装', '羊毛', '待改', NOW()),
('GX-1002', '周慕白', '裙',   '丝绸', '修改中', NOW()),
('GX-1003', '陈嘉树', '裤',   '棉',   '待取', NOW()),
('GX-1004', '苏晚晴', '大衣', '羊毛', '待取', NOW()),
('GX-1005', '许清欢', '西装', '涤纶', '已取', NOW()),
('GX-1006', '江予安', '裙',   '麻',   '待取', NOW());

INSERT IGNORE INTO alteration (garment_id, item, price, status, created_at) VALUES
(1, '长短', 80, '待开工', NOW()),
(1, '宽窄', 0,  '待开工', NOW()),              -- 异常态：报价为 0（用于触发「报价>0」拒绝）
(2, '宽窄', 60, '已完成', NOW()),
(2, '换拉链', 30, '已完成', NOW()),
(3, '长短', 50, '待开工', NOW()),             -- 异常态：衣物待取但工单未完成（取衣拦截）
(4, '换扣', 40, '已完成', NOW()),             -- 正常：衣物待取且工单完成
(6, '换扣', 20, '已完成', NOW());             -- 正常：衣物待取且工单完成（但预约未付清）

INSERT IGNORE INTO fabric (name, category, stock, unit, threshold, created_at) VALUES
('进口里布',   '里布', 120, '米', 20, NOW()),
('硬衬',       '衬布', 80,  '米', 10, NOW()),
('金属拉链',   '拉链', 5,   '条', 10, NOW()),  -- 异常态：库存低于下限（领用拦截）
('树脂纽扣',   '纽扣', 0,   '颗', 50, NOW()),  -- 异常态：库存为 0（领用拦截）
('装饰花边',   '花边', 60,  '米', 15, NOW()),
('缝纫配线',   '配线', 200, '卷', 30, NOW());

INSERT IGNORE INTO appointment (garment_id, customer, appoint_time, pickup_time, paid, status, created_at) VALUES
(1, '林小满', DATE_SUB(CURDATE(), INTERVAL 7 DAY), NULL, 0, '已预约', NOW()),
(4, '苏晚晴', DATE_SUB(CURDATE(), INTERVAL 5 DAY), NULL, 1, '已预约', NOW()),  -- 可取衣：待取+工单完成+已付清
(3, '陈嘉树', DATE_SUB(CURDATE(), INTERVAL 4 DAY), NULL, 1, '已预约', NOW()),  -- 异常态：工单未完成，取衣拦截
(6, '江予安', DATE_SUB(CURDATE(), INTERVAL 3 DAY), NULL, 0, '已预约', NOW());  -- 异常态：未付清，取衣拦截
