package com.aicode.core.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TemplateEngineEnumTest {

    @Test
    @DisplayName("getTemplate 大小写不敏感")
    void getTemplate_caseInsensitive() {
        assertEquals(TemplateEngineEnum.Freemarker, TemplateEngineEnum.getTemplate("Freemarker"));
        assertEquals(TemplateEngineEnum.Freemarker, TemplateEngineEnum.getTemplate("freemarker"));
        assertEquals(TemplateEngineEnum.Freemarker, TemplateEngineEnum.getTemplate("FREEMARKER"));
        assertEquals(TemplateEngineEnum.Beetl, TemplateEngineEnum.getTemplate("Beetl"));
        assertEquals(TemplateEngineEnum.Beetl, TemplateEngineEnum.getTemplate("beetl"));
    }

    @Test
    @DisplayName("getTemplate 未知引擎返回 null（Generator 会回退 Freemarker）")
    void getTemplate_unknown_returnsNull() {
        assertNull(TemplateEngineEnum.getTemplate("Velocity"));
        assertNull(TemplateEngineEnum.getTemplate("Thymeleaf"));
        assertNull(TemplateEngineEnum.getTemplate(""));
        assertNull(TemplateEngineEnum.getTemplate(null));
    }
}
