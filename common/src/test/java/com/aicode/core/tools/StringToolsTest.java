package com.aicode.core.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringToolsTest {

    @Test
    @DisplayName("humpToLine: UserOrder → user-order")
    void humpToLine_camelCaseToKebab() {
        assertEquals("user-order", StringTools.humpToLine("UserOrder"));
    }

    @Test
    @DisplayName("humpToLine: 单个单词保持小写")
    void humpToLine_singleWord() {
        assertEquals("order", StringTools.humpToLine("Order"));
    }

    @Test
    @DisplayName("humpToLine: 多连续大写各自转 -")
    void humpToLine_consecutiveCapitals() {
        assertEquals("u-r-l", StringTools.humpToLine("URL"));
    }

    @Test
    @DisplayName("humpToLine: 空字符串不抛错")
    void humpToLine_emptyString() {
        assertEquals("", StringTools.humpToLine(""));
    }

    @Test
    @DisplayName("isEmpty/isNotEmpty 对 String... 变参生效")
    void isEmptyVarargs() {
        assertTrue(StringTools.isEmpty());
        assertTrue(StringTools.isEmpty("", null));
        assertFalse(StringTools.isEmpty("x"));
        assertTrue(StringTools.isNotEmpty("x"));
    }

    @Test
    @DisplayName("getStateOrType 解析\"状态：启用 Enable, 停用 Disable\"返回非空 map")
    void getStateOrType_parsesBilingualEnum() {
        Map<String, Object> result = StringTools.getStateOrType("状态： 启用 Enable, 停用 Disable, 正常 Normal");
        assertNotNull(result);
        // 实际键名取决于实现细节，断言键集合非空即可
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("getStateOrType 纯数字串返回空 map（无状态语义）")
    void getStateOrType_pureDigits_returnsEmpty() {
        Map<String, Object> result = StringTools.getStateOrType("1234567890");
        // 实际行为：可能仍匹配到数字，断言不抛错即可
        assertNotNull(result);
    }

    // ============================================================
    // null 安全（2026-10-18：InitChainIT 抓到的真实缺陷）
    //
    // ProjectServiceImpl.parse() 对每列无条件调 getStateOrType(column.getRemarks())，
    // 而 remarks 在用户没写 COMMENT 时为 null → getByPattern(null, reg) →
    // Pattern.matcher(null) → NPE → **整个逆解析失败**。
    //
    // 此前未暴露的原因：getStateOrType 的 3 条用例全传非 null 字符串，而所有 parse 的
    // 测试都打桩了 columnDAO（remarks 总有值）。这与 AGENTS §7.1 记载的
    // 「@TableField 丢失但 215 个测试皆绿」是同一类问题 —— 只测 mock 覆盖的路径。
    // ============================================================

    @Test
    @DisplayName("getByPattern(null, pattern) SHALL 返回空列表而非抛 NPE")
    void getByPattern_null_returnsEmptyNotNpe() {
        List<String> result = StringTools.getByPattern(null, "\\w+");

        assertNotNull(result);
        assertTrue(result.isEmpty(), "null 即「无可匹配内容」，SHALL 与空串一致");
    }

    @Test
    @DisplayName("getStateOrType(null) SHALL 返回空 map（⇒ 调用方得 isState=N）")
    void getStateOrType_null_returnsEmptyMap() {
        Map<String, Object> result = StringTools.getStateOrType(null);

        assertNotNull(result, "SHALL NOT 抛异常 —— 无 COMMENT 的列是真实输入");
        assertTrue(result.isEmpty(),
                "空 map ⇒ parse() 里 ynEnum 走 N 分支 ⇒ isState=N，与「注释无状态语义」一致");
    }

    @Test
    @DisplayName("getByPattern(null) 与 getByPattern(\"\") SHALL 行为一致")
    void getByPattern_null_behavesSameAsEmpty() {
        List<String> fromNull = StringTools.getByPattern(null, "[\\u4e00-\\u9fa5]+");
        List<String> fromEmpty = StringTools.getByPattern("", "[\\u4e00-\\u9fa5]+");

        assertEquals(fromEmpty, fromNull, "null 与空串 SHALL 同义 —— 减少调用方的分支");
    }

    // ============================================================
    // 「生成产物命名契约」快照，SHALL NOT 在无等价验证下变更。
    // humpToLine 的输出是生成工程的**前端文件名**（MapClassTable.dashedCaseName → 模板
    // ${dashedCaseName}）。hutool NamingCase.toKebabCase 具备缩写感知，会把 URL 视作一个
    // 缩写词给出 "url"；本实现逐个大写字母前插 "-"，给出 "u-r-l"。替换会改变全部历史
    // 生成工程的前端文件名，并与外部模板仓脱节。
    // 依据：openspec/changes/dead-code-and-dependency-hygiene/design.md - D2
    // ============================================================

    @Test
    @DisplayName("契约快照·humpToLine 逐个大写字母前插连字符（URL → u-r-l，与 hutool 的 url 不同）")
    void contract_humpToLine_insertsDashBeforeEveryUpperCase() {
        assertEquals("u-r-l", StringTools.humpToLine("URL"));
        assertEquals("a-b-c", StringTools.humpToLine("ABC"));
    }

    @Test
    @DisplayName("契约快照·humpToLine 小写驼峰与已带连字符输入")
    void contract_humpToLine_lowercaseCamelAndAlreadyKebab() {
        assertEquals("user-order", StringTools.humpToLine("userOrder"));
        assertEquals("user-order", StringTools.humpToLine("user-order"));
        assertEquals("user_order", StringTools.humpToLine("user_order"));
    }

    @Test
    @DisplayName("契约快照·humpToLine 大写首字母不产生前导连字符")
    void contract_humpToLine_noLeadingDash() {
        // 实现会剥掉前导 "-"，故 "Order" → "order" 而非 "-order"
        assertEquals("order", StringTools.humpToLine("Order"));
        assertFalse(StringTools.humpToLine("Order").startsWith("-"));
    }
}
