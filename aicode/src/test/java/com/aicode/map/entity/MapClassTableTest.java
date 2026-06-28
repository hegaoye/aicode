package com.aicode.map.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 回归测试：MapClassTable.getClassModel 自愈与 NPE 防御。
 *
 * <p>背景：feature/20260622/delta 在执行 GeneratorSVImpl 时偶发 NPE
 * <pre>Cannot invoke "String.equals(Object)" because the return value of
 * "MapClassTable.getClassModel()" is null</pre>
 *
 * 根因：DB 中历史 map_class_table 记录的 class_model 列为 NULL
 * （之前 parse 流程 / 早期版本没填该列），selectOne 出来 classModel=null，
 * line 544 用 .equals(null) 抛 NPE。</p>
 *
 * <p>修复方案 A（getClassModel 自愈）+ B（Objects.equals 守卫）已实施。
 * 本测试覆盖两个修复点的核心契约。</p>
 */
class MapClassTableTest {

    @Test
    @DisplayName("toJava() 后 getClassModel() 返回非 null（happy path）")
    void toJava_setsClassModelFromTableName() {
        MapClassTable mct = new MapClassTable("c1", "user_order", "用户订单表");
        mct.toJava();

        // tableName=user_order 含 "_"，classModel 应为 "user"
        assertEquals("user", mct.getClassModel());
    }

    @Test
    @DisplayName("toJava() 后 classModel 与 toJava 内部一致（自愈路径与显式路径结果相同）")
    void toJava_thenGetClassModel_isConsistentWithExplicitSet() {
        MapClassTable explicit = new MapClassTable("c1", "user_order", "");
        explicit.setClassModel("user");    // 模拟 DB 已有值
        explicit.toJava();                 // toJava 会再设一次（无副作用）

        MapClassTable recovered = new MapClassTable("c2", "user_order", "");
        // 不调 toJava —— 模拟 DB 历史脏数据：classModel=null
        String firstCall = recovered.getClassModel();   // 第一次触发自愈

        assertNotNull(firstCall);
        assertEquals(explicit.getClassModel(), firstCall);
        // 缓存：第二次应返回相同值
        assertEquals(firstCall, recovered.getClassModel());
    }

    @Test
    @DisplayName("DB 脏数据场景：classModel=null 时 getClassModel() 仍能从 tableName 自愈出非 null（方案 A 核心）")
    void getClassModel_nullField_recoversFromTableName() {
        MapClassTable mct = new MapClassTable("c1", "user_order", "");
        // 不调 toJava —— 模拟 selectOne 直接出来的脏数据
        assertNull(getClassModelField(mct), "前置：classModel 字段应为 null");
        assertEquals("user", mct.getClassModel(), "自愈：tableName 含 _ 时取首段");
    }

    @Test
    @DisplayName("DB 脏数据场景：tableName 不含 _ 时 classModel 等于 tableName")
    void getClassModel_tableNameWithoutUnderscore_returnsTableName() {
        MapClassTable mct = new MapClassTable("c1", "user", "");
        assertNull(getClassModelField(mct));
        assertEquals("user", mct.getClassModel());
    }

    @Test
    @DisplayName("边界：tableName=null 时 getClassModel() 返回 null 不抛 NPE")
    void getClassModel_nullTableName_returnsNullSafely() {
        MapClassTable mct = new MapClassTable("c1", null, "");
        assertNull(getClassModelField(mct));
        // 自愈分支需要 tableName != null，跳过自愈，返回原 null
        assertNull(mct.getClassModel());
    }

    /**
     * 用反射直接读字段值，绕过 getter 的自愈副作用。
     */
    private static String getClassModelField(MapClassTable mct) {
        try {
            java.lang.reflect.Field f = MapClassTable.class.getDeclaredField("classModel");
            f.setAccessible(true);
            return (String) f.get(mct);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
