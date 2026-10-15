package com.aicode.core.tools.core.typemapping;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DatabaseDataTypesUtils} 的类型判定与映射单测。
 *
 * <p>重点守护 2026-10-15 新增的 null 防护：H2 2.x 移除了
 * {@code INFORMATION_SCHEMA.COLUMNS.TYPE_NAME}，改用 {@code DATA_TYPE}；
 * 若查询未用 {@code AS} 显式别名，实体属性 {@code typeName} 会静默为 {@code null}，
 * 原实现随即在 {@code javaType.endsWith(...)} 抛 NPE —— 把 SQL 层错误伪装成空指针。
 */
class DatabaseDataTypesUtilsTest {

    // ---------- null 防护 ----------

    @Test
    @DisplayName("五个 isXxx 判定方法对 null 入参返回 false，不抛 NPE")
    void predicates_nullInput_returnsFalseWithoutNpe() {
        assertFalse(DatabaseDataTypesUtils.isDate(null));
        assertFalse(DatabaseDataTypesUtils.isIntegerNumber(null));
        assertFalse(DatabaseDataTypesUtils.isFloatNumber(null));
        assertFalse(DatabaseDataTypesUtils.isPrimaryKey(null));
        assertFalse(DatabaseDataTypesUtils.isStateOrType(null));
    }

    @Test
    @DisplayName("getPreferredJavaType(null) 返回 Object 而不抛 NPE")
    void getPreferredJavaType_null_returnsObject() {
        assertEquals("java.lang.Object", DatabaseDataTypesUtils.getPreferredJavaType((String) null));
    }

    // ---------- 既有判定行为（防止 null 防护误伤） ----------

    @Test
    @DisplayName("isDate 识别 java.util.Date / java.sql.Timestamp 与小写 SQL 类型名")
    void isDate_recognizes() {
        assertTrue(DatabaseDataTypesUtils.isDate("java.util.Date"));
        assertTrue(DatabaseDataTypesUtils.isDate("java.sql.Timestamp"));
        assertTrue(DatabaseDataTypesUtils.isDate("date"));
        assertTrue(DatabaseDataTypesUtils.isDate("DATETIME"));
        assertFalse(DatabaseDataTypesUtils.isDate("java.lang.String"));
        assertFalse(DatabaseDataTypesUtils.isDate("java.lang.Integer"));
    }

    @Test
    @DisplayName("isIntegerNumber / isFloatNumber 区分包装类型与基本类型")
    void numberPredicates() {
        assertTrue(DatabaseDataTypesUtils.isIntegerNumber("java.lang.Integer"));
        assertTrue(DatabaseDataTypesUtils.isIntegerNumber("java.lang.Long"));
        assertTrue(DatabaseDataTypesUtils.isIntegerNumber("int"));
        assertFalse(DatabaseDataTypesUtils.isIntegerNumber("java.lang.Double"));

        assertTrue(DatabaseDataTypesUtils.isFloatNumber("java.lang.Double"));
        assertTrue(DatabaseDataTypesUtils.isFloatNumber("java.math.BigDecimal"));
        assertTrue(DatabaseDataTypesUtils.isFloatNumber("double"));
        assertFalse(DatabaseDataTypesUtils.isFloatNumber("java.lang.Integer"));
    }

    @Test
    @DisplayName("isPrimaryKey 识别 PRI / NO")
    void isPrimaryKey_recognizes() {
        assertTrue(DatabaseDataTypesUtils.isPrimaryKey("PRI"));
        assertTrue(DatabaseDataTypesUtils.isPrimaryKey("no"));
        assertFalse(DatabaseDataTypesUtils.isPrimaryKey("YES"));
    }

    @Test
    @DisplayName("isStateOrType 识别 state / status")
    void isStateOrType_recognizes() {
        assertTrue(DatabaseDataTypesUtils.isStateOrType("order_state"));
        assertTrue(DatabaseDataTypesUtils.isStateOrType("OrderStatus"));
        assertFalse(DatabaseDataTypesUtils.isStateOrType("order_no"));
    }

    // ---------- SQL 类型映射 ----------

    @Test
    @DisplayName("getPreferredJavaType 大小写不敏感地映射常用 SQL 类型")
    void getPreferredJavaType_mapsCommonTypes() {
        assertEquals("java.lang.Long", DatabaseDataTypesUtils.getPreferredJavaType("bigint"));
        assertEquals("java.lang.Long", DatabaseDataTypesUtils.getPreferredJavaType("BIGINT"));
        assertEquals("java.lang.String", DatabaseDataTypesUtils.getPreferredJavaType("VARCHAR"));
        assertEquals("java.lang.Byte", DatabaseDataTypesUtils.getPreferredJavaType("tinyint"));
    }

    @Test
    @DisplayName("未登记的 SQL 类型回退为 java.lang.Object，不抛异常")
    void getPreferredJavaType_unknownType_returnsObject() {
        assertEquals("java.lang.Object", DatabaseDataTypesUtils.getPreferredJavaType("some_unknown_type"));
    }
}
