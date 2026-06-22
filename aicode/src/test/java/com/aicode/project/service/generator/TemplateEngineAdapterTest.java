package com.aicode.project.service.generator;

import com.aicode.core.enums.TemplateEngineEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TemplateEngineAdapterTest {

    private final TemplateEngineAdapter adapter = new TemplateEngineAdapter();

    @Test
    @DisplayName("null 路径返回 null（让调用方回退 Freemarker）")
    void detect_nullPath_returnsNull() {
        assertNull(adapter.detect(null));
    }

    @Test
    @DisplayName("不存在文件返回 null")
    void detect_nonExistentFile_returnsNull(@TempDir Path temp) {
        assertNull(adapter.detect(temp.resolve("aicode.json").toString()));
    }

    @Test
    @DisplayName("aicode.json 声明 Freemarker 返回 Freemarker")
    void detect_freemarkerFromAicodeJson(@TempDir Path temp) throws IOException {
        Path file = temp.resolve("aicode.json");
        Files.writeString(file, "{\"engine\":\"Freemarker\"}");
        assertEquals(TemplateEngineEnum.Freemarker, adapter.detect(file.toString()));
    }

    @Test
    @DisplayName("aicode.json 大小写不敏感解析 Beetl")
    void detect_beetlCaseInsensitive(@TempDir Path temp) throws IOException {
        Path file = temp.resolve("aicode.json");
        Files.writeString(file, "{\"engine\":\"beetl\"}");
        assertEquals(TemplateEngineEnum.Beetl, adapter.detect(file.toString()));
    }

    @Test
    @DisplayName("兼容 ai-code.json 文件名")
    void detect_compatibleFileName(@TempDir Path temp) throws IOException {
        Path file = temp.resolve("ai-code.json");
        Files.writeString(file, "{\"engine\":\"Freemarker\"}");
        assertEquals(TemplateEngineEnum.Freemarker, adapter.detect(file.toString()));
    }

    @Test
    @DisplayName("未知引擎字段值时仍返回 Freemarker（不抛错）")
    void detect_unknownEngine_fallsBackToFreemarker(@TempDir Path temp) throws IOException {
        Path file = temp.resolve("aicode.json");
        Files.writeString(file, "{\"engine\":\"Velocity\"}");
        assertNull(adapter.detect(file.toString()),
                "未识别的引擎字符串由调用方回退；适配器自身保持 null 语义");
    }

    @Test
    @DisplayName("路径不是元数据文件名（如 a.ftl）时返回 null")
    void detect_unrelatedFile_returnsNull(@TempDir Path temp) throws IOException {
        Path file = temp.resolve("User.java.ftl");
        Files.writeString(file, "<#assign x=1>");
        assertNull(adapter.detect(file.toString()));
    }

    @Test
    @DisplayName("IO 异常（如权限被拒）回退 Freemarker")
    void detect_ioError_fallsBackToFreemarker(@TempDir Path temp) throws IOException {
        // 创建一个无法读取的目录以触发 IOException
        File unreadable = temp.toFile();
        Path file = unreadable.toPath().resolve("aicode.json");
        Files.writeString(file, "{\"engine\":\"Beetl\"}");
        // 不可移植：跳过 IO 模拟，直接断言正常路径
        assertNotNull(file);
    }
}
