package com.aicode.config.template.config;

import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * 初始化 Beetl模板对象用于模板渲染使用
 * Created by lixin on 20/3/8.
 */
@Slf4j
@org.springframework.context.annotation.Configuration
public class BeetlConfig {

    @Bean
    public GroupTemplate beetlTemplate() {
        //初始化代码
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg = null;
        try {
            cfg = Configuration.defaultConfiguration();
            cfg.setPlaceholderStart("$");
            cfg.setPlaceholderEnd("$");
            cfg.setStatementStart("/***");
            cfg.setStatementEnd("***/");
        } catch (IOException e) {
            log.error("异常", e);
        }
        GroupTemplate groupTemplate = new GroupTemplate(resourceLoader, cfg);
        return groupTemplate;

    }
}
