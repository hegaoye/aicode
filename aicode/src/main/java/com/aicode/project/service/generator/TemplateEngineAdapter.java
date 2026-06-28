package com.aicode.project.service.generator;

import com.aicode.config.template.Configuration;
import com.aicode.core.enums.TemplateEngineEnum;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 模板引擎适配器：从模板仓库根的元数据文件推断出当前模板所用引擎。
 *
 * <p>支持 4 种文件名（兼容历史拼写）：{@code aicode.json} / {@code aicode} /
 * {@code ai-code.json} / {@code ai-code}。缺省回退 {@link TemplateEngineEnum#Freemarker}。</p>
 */
@Slf4j
@Component
public class TemplateEngineAdapter {

    private static final List<String> COMPATIBLE_FILE_NAMES = Arrays.asList(
            "aicode.json", "aicode", "ai-code.json", "ai-code");

    /**
     * 给定模板仓库内一个文件的绝对路径，若该文件命中元数据文件名且可解析，
     * 返回声明的引擎；否则返回 {@code null}（调用方需回退 Freemarker）。
     */
    public TemplateEngineEnum detect(String filePath) {
        log.info("检查 aicode.json 路径 : {}", filePath);
        if (filePath == null) {
            return null;
        }
        File candidate = null;
        for (String name : COMPATIBLE_FILE_NAMES) {
            if (filePath.endsWith(name)) {
                File f = new File(filePath);
                if (f.exists()) {
                    candidate = f;
                    break;
                }
            }
        }
        if (candidate == null) {
            return null;
        }
        try {
            String json = FileUtils.readFileToString(candidate);
            log.info("读取模板引擎声明文件内容 : {}", json);
            Configuration configuration = JSON.parseObject(json, Configuration.class);
            return TemplateEngineEnum.getTemplate(configuration.getEngine());
        } catch (IOException e) {
            log.error("读取模板引擎声明文件失败: {}", candidate.getAbsolutePath(), e);
            return TemplateEngineEnum.Freemarker;
        }
    }
}
