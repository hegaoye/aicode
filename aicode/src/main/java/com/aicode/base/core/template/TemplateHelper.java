package com.aicode.base.core.template;


import com.aicode.base.core.TemplateData;

/**
 * 模板引擎接口，实现此接口可以自动切换模板引擎
 * Created by lixin on 20/3/8.
 */
public interface TemplateHelper {
    /**
     * 模板引擎接口，其余 helper自动实现此接口，进行模板的生成自动切换
     *
     * @param templateData   模板数据对象
     * @param targetFilePath 目标文件路径
     * @param templatePath   模板路径
     */
    String generate(TemplateData templateData, String targetFilePath, String templatePath);
}
