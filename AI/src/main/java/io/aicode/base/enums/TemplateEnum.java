package io.aicode.base.enums;

/**
 * 模板引擎 枚举说明
 * Created by chendehui on 20/3/8.
 */
public enum TemplateEnum {
    Freemarker,
    Beetl,;

    public static TemplateEnum getTemplate(String template) {
        for (TemplateEnum templateEnum : TemplateEnum.values()) {
            if (templateEnum.name().equalsIgnoreCase(template)) {
                return templateEnum;
            }
        }
        return null;
    }
}
