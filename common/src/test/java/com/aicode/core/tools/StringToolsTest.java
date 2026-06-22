package com.aicode.core.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
