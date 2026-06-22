package com.aicode.core.tools.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringHelperTest {

    @Test
    @DisplayName("user_order → UserOrder")
    void toJavaClassName_snakeCaseToUpperCamel() {
        assertEquals("UserOrder", StringHelper.toJavaClassName("user_order"));
    }

    @Test
    @DisplayName("order → Order（无下划线）")
    void toJavaClassName_singleWord() {
        assertEquals("Order", StringHelper.toJavaClassName("order"));
    }

    @Test
    @DisplayName("已是大驼峰保持稳定")
    void toJavaClassName_alreadyCamelCase() {
        assertEquals("UserOrder", StringHelper.toJavaClassName("UserOrder"));
    }

    @Test
    @DisplayName("a_b_c_d → Abcd 风格（首字母大写，余下合并小写）")
    void toJavaClassName_multipleUnderscores() {
        // makeAllWordFirstLetterUpperCase 把首段大写、后续段拼接为小写（preStr.length()==1 路径）
        String result = StringHelper.toJavaClassName("a_b_c_d");
        assertEquals("Abcd", result);
    }

    @Test
    @DisplayName("toJavaVariableName = uncapitalize(toJavaClassName)")
    void toJavaVariableName_uncapitalizesClassName() {
        assertEquals("userOrder", StringHelper.toJavaVariableName("user_order"));
        assertEquals("order", StringHelper.toJavaVariableName("order"));
    }

    @Test
    @DisplayName("空字符串与 null——null 安全（修复了 makeAllWordFirstLetterUpperCase NPE）")
    void toJavaClassName_emptyAndNull() {
        assertEquals("", StringHelper.toJavaClassName(""));
        assertNull(StringHelper.toJavaClassName(null));
    }
}
