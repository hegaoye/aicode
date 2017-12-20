package com.rzhkj.base.core;

import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

public class FreemarkerHelper {
    public static void main(String[] args) throws IOException, TemplateException {
        Map<String, Object> map = Maps.newHashMap();
        map.put("className", "Hegaoye");
        String targetFilePath = "C:\\workspaces\\AI-Code\\AI\\src\\main\\webapp\\workspace\\szh\\sv\\src\\main\\java\\com\\szh\\ctrl\\" + map.get("className").toString() + ".java";
        generate(map, targetFilePath, "test.java", "C:\\workspaces\\AI-Code\\AI\\src\\main\\webapp\\templates");
    }


    /**
     * 生成源文件
     *
     * @param model            模型数据
     * @param targetFilePath   目标路径 /xxxx/xxx/{ClassName.java}
     * @param templateFileName 模板文件名 [temp.java|temp.xml|temp.properties]
     * @param templatePath     模板路径 [/xxx/xxxx|/xxx/xxxx/]
     */
    public static void generate(Map<String, Object> model, String targetFilePath, String templateFileName, String templatePath) {
        try {
            Configuration configuration = new Configuration();
            configuration.setDirectoryForTemplateLoading(new File(templatePath));
            configuration.setDefaultEncoding("UTF-8");
            Template temp = configuration.getTemplate(templateFileName);
            Writer out = new OutputStreamWriter(new FileOutputStream(targetFilePath));
            temp.process(model, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }
}
