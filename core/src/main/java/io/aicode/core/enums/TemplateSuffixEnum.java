/*
 *
 *                       http://www.aicode.io
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package io.aicode.core.enums;

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
    Yml(".yml"),
    ;
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
