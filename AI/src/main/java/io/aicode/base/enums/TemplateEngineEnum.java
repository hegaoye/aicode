package io.aicode.base.enums;

/**
 * 模板引擎 枚举说明
 * Created by chendehui on 20/3/8.
 */
public enum TemplateEngineEnum {
    Freemarker,
    Beetl,;

    public static TemplateEngineEnum getTemplate(String template) {
        for (TemplateEngineEnum templateEngineEnum : TemplateEngineEnum.values()) {
            if (templateEngineEnum.name().equalsIgnoreCase(template)) {
                return templateEngineEnum;
            }
        }
        return null;
    }
}
