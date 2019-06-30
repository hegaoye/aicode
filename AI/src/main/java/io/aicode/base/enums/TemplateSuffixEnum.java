/*
 *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.
 */

package io.aicode.base.enums;

/**
 * 允许的模板后缀
 * Created by lixin on 2017/6/7.
 */
public enum TemplateSuffixEnum {
    Java(".java"),
    JavaScript(".js"),
    TypeScript(".ts"),
    Freemarker(".ftl"),
    Httl(".httl"),
    Gradle(".gradle"),
    Xml(".xml"),
    Sql(".sql"),
    Md(".md"),
    Property(".properties"),
    Yml(".yml"),;
    public String suffix;

    TemplateSuffixEnum(String suffix) {
        this.suffix = suffix;
    }

    public static TemplateSuffixEnum getSuffix(String suffix) {
        for (TemplateSuffixEnum suffixEnum : TemplateSuffixEnum.values()) {
            if (suffixEnum.name().equalsIgnoreCase(suffix)) {
                return suffixEnum;
            }
        }
        return null;
    }
}
