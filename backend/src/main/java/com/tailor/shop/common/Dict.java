package com.tailor.shop.common;

import java.util.List;

/**
 * 裁缝改衣店业务词典与规则参数表：衣物品类/材质/状态机、改衣项目、面料类别、
 * 预约取衣状态。规则集中在此，便于出题时要求调整或扩展。
 */
public final class Dict {

    private Dict() {
    }

    // ---------------- 衣物台账 ----------------
    /** 西装 / 裙 / 裤 / 大衣 */
    public static final List<String> GARMENT_CATEGORIES = List.of("西装", "裙", "裤", "大衣");

    /** 羊毛 / 棉 / 涤纶 / 丝绸 / 麻 */
    public static final List<String> GARMENT_MATERIALS = List.of("羊毛", "棉", "涤纶", "丝绸", "麻");

    /** 待改 → 修改中 → 待取 → 已取（已取为终态不可回退） */
    public static final List<String> GARMENT_STATUS = List.of("待改", "修改中", "待取", "已取");

    // ---------------- 改衣工单 ----------------
    /** 长短 / 宽窄 / 换拉链 / 换扣 */
    public static final List<String> ALTER_ITEMS = List.of("长短", "宽窄", "换拉链", "换扣");

    /** 待开工 / 已完成 */
    public static final List<String> ALTER_STATUS = List.of("待开工", "已完成");

    // ---------------- 面料库存 ----------------
    /** 里布 / 衬布 / 拉链 / 纽扣 / 花边 / 配线 */
    public static final List<String> FABRIC_CATEGORIES = List.of("里布", "衬布", "拉链", "纽扣", "花边", "配线");

    // ---------------- 预约与取衣 ----------------
    /** 已预约 / 已取衣 */
    public static final List<String> APPOINTMENT_STATUS = List.of("已预约", "已取衣");

    // ---------------- 取值方法 ----------------

    public static boolean isValidGarmentCategory(String s) {
        return s != null && GARMENT_CATEGORIES.contains(s);
    }

    public static boolean isValidGarmentMaterial(String s) {
        return s == null || s.isBlank() || GARMENT_MATERIALS.contains(s);
    }

    public static boolean isValidGarmentStatus(String s) {
        return s != null && GARMENT_STATUS.contains(s);
    }

    public static boolean isValidAlterItem(String s) {
        return s != null && ALTER_ITEMS.contains(s);
    }

    public static boolean isValidAlterStatus(String s) {
        return s != null && ALTER_STATUS.contains(s);
    }

    public static boolean isValidFabricCategory(String s) {
        return s != null && FABRIC_CATEGORIES.contains(s);
    }

    public static boolean isValidAppointmentStatus(String s) {
        return s != null && APPOINTMENT_STATUS.contains(s);
    }

    /**
     * 衣物状态机：严格单向前进，已取为终态不可回退；同态视为无操作。
     */
    public static boolean canTransferGarment(String from, String to) {
        if (!isValidGarmentStatus(from) || !isValidGarmentStatus(to)) {
            return false;
        }
        if ("已取".equals(from)) {
            return false;
        }
        int fi = GARMENT_STATUS.indexOf(from);
        int ti = GARMENT_STATUS.indexOf(to);
        return ti >= fi;
    }
}
