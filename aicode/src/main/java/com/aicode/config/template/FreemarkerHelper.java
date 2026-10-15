package com.aicode.config.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson2.JSON;

@Slf4j
@Service
public class FreemarkerHelper implements TemplateHelper {


    /**
     * 生成源文件
     *
     * @param templateData   模型数据
     * @param targetFilePath 目标路径 /xxxx/xxx/{ClassName.java}
     * @param templatePath   模板路径 [/xxx/xxxx|/xxx/xxxx/]
     */
    @Override
    public String generate(TemplateData templateData, String targetFilePath, String templatePath) {
        Writer out = null;
        templatePath = templatePath.replace("//", "/").replace("\\", "/");
        String templateFileName = templatePath.substring(templatePath.lastIndexOf("/") + 1);
        templatePath = templatePath.substring(0, templatePath.lastIndexOf("/"));
        targetFilePath = targetFilePath.replace("\\", "/").replace("//", "/");
        String filePath = targetFilePath.substring(0, targetFilePath.lastIndexOf("/"));

        File dirFile = new File(filePath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        Configuration configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");
        try {
            configuration.setDirectoryForTemplateLoading(new File(templatePath));
            Template temp = configuration.getTemplate(templateFileName);
            out = new OutputStreamWriter(new FileOutputStream(targetFilePath), Charset.forName("UTF-8"));
            // 保留 fastjson2 的 bean -> String -> Map 往返，SHALL NOT 换成 hutool BeanUtil.beanToMap：
            // fastjson2 按 **getter** 命名产出键（basePackage / className / dashedCaseName），
            // 而 hutool 按 **字段** 产出键（base_package / classname ...），两者键集不一致，
            // 换过去会让所有外部模板仓的 ${className} 等变量取不到值 —— 生成产物静默改变。
            // 回归守护见 TemplateDataExpansionTest。
            Map<String, Object> param = JSON.parseObject(JSON.toJSONString(templateData), Map.class);
            param.put("package", templateData.getBasePackage());
            temp.process(param, out);
        } catch (IOException e) {
            log.error("异常", e);
            return e.getMessage();
        } catch (TemplateException e) {
            log.error("异常", e);
            return e.getMessage();
        }
        return "success";
    }
}
