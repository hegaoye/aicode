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

    // ============================================================
    // 以下为「生成产物命名契约」快照，SHALL NOT 在无等价验证下变更。
    //
    // 背景：这些方法的输出直接是生成工程的类名、字段名与枚举常量名，并与外部模板仓
    // `aicode_template` 的约定绑定。第三方库的「等价 API」在下列形态上均产生不同输出：
    //   toJavaClassName  hutool NamingCase.toPascalCase("a_b_c_d") → "ABCD"（本实现 "Abcd"）
    //   capitalize       commons-lang3 StringUtils.capitalize("USER") → "User"（本实现 "USER"）
    //   humpToLine       hutool NamingCase.toKebabCase("URL") → "url"（本实现 "u-r-l"）
    // 替换任一项都意味着所有历史生成工程的类名与前端文件名发生不可逆变化。
    //
    // 依据：openspec/changes/dead-code-and-dependency-hygiene/design.md - D2
    //      openspec/specs/dependency-hygiene/spec.md「生成产物的命名形态 SHALL 被测试固化」
    // ============================================================

    @Test
    @DisplayName("契约快照·全大写缩写：USER_ORDER → UserOrder（toUnderscoreName 先折叠为小写）")
    void contract_toJavaClassName_allUpperSnake() {
        assertEquals("UserOrder", StringHelper.toJavaClassName("USER_ORDER"));
    }

    @Test
    @DisplayName("契约快照·全大写无下划线：URL → Url（hutool toPascalCase 会给 URL，此处必须为 Url）")
    void contract_toJavaClassName_allUpperNoUnderscore() {
        assertEquals("Url", StringHelper.toJavaClassName("URL"));
    }

    @Test
    @DisplayName("契约快照·驼峰输入按驼峰切分：userName → UserName")
    void contract_toJavaClassName_camelInput() {
        assertEquals("UserName", StringHelper.toJavaClassName("userName"));
    }

    @Test
    @DisplayName("契约快照·三段下划线：a_b_c → Abc（第二段起拼接小写，不做分词）")
    void contract_toJavaClassName_threeSegments() {
        assertEquals("Abc", StringHelper.toJavaClassName("a_b_c"));
    }

    @Test
    @DisplayName("契约快照·数字后缀：order_detail_2 → OrderDetail2")
    void contract_toJavaClassName_numericSuffix() {
        assertEquals("OrderDetail2", StringHelper.toJavaClassName("order_detail_2"));
    }

    @Test
    @DisplayName("契约快照·toJavaVariableName：UserOrder → userOrder，Abcd → abcd，Url → url")
    void contract_toJavaVariableName() {
        assertEquals("userOrder", StringHelper.toJavaVariableName("user_order"));
        assertEquals("abcd", StringHelper.toJavaVariableName("a_b_c_d"));
        assertEquals("url", StringHelper.toJavaVariableName("URL"));
    }

    @Test
    @DisplayName("契约快照·capitalize 只改首字符：aBC → ABC、USER → USER、user → User")
    void contract_capitalize_onlyTouchesFirstCharacter() {
        // commons-lang3 的 StringUtils.capitalize 会把其余字符全部小写（"aBC" → "Abc"，
        // "USER" → "User"），会改掉 TemplateData 生成的枚举常量名，故不得替换。
        assertEquals("ABC", StringHelper.capitalize("aBC"));
        assertEquals("USER", StringHelper.capitalize("USER"));
        assertEquals("User", StringHelper.capitalize("user"));
        assertEquals("", StringHelper.capitalize(""));
        assertNull(StringHelper.capitalize(null));
    }
}
