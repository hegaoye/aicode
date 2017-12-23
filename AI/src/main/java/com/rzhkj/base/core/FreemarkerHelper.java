package com.rzhkj.base.core;

import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

public class FreemarkerHelper {
    public static void main(String[] args) throws IOException, TemplateException {
        Map<String, Object> map = Maps.newHashMap();
        map.put("fileName", "Hegaoye");
        map.put("className", "public\tclass\t" + map.get("fileName") + "\t");
        map.put("extends", "extends\tPage\timplements\tSerializable\t");
        map.put("basePackage", "package\tcom.szh.test.ctrl;");
        map.put("import", "import\tcom.aixin.core.entity.Page;\nimport\torg.apache.commons.lang.builder.ToStringBuilder;\nimport\torg.apache.commons.lang.builder.ToStringStyle;\nimport\tjava.io.Serializable;");
        String targetFilePath = "C:\\workspaces\\AI-Code\\AI\\src\\main\\webapp\\workspace\\szh\\sv\\src\\main\\java\\com\\szh\\test\\ctrl\\" + map.get("fileName").toString() + ".java";
        generate(map, targetFilePath, "Test.java", "C:\\workspaces\\AI-Code\\AI\\src\\main\\webapp\\templates");
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
        Writer out = null;
        try {
            targetFilePath = targetFilePath.replace("\\", "/");
            String filePath = targetFilePath.substring(0, targetFilePath.lastIndexOf("/"));

            File dirFile = new File(filePath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            Configuration configuration = new Configuration();
            configuration.setDefaultEncoding("UTF-8");
            configuration.setDirectoryForTemplateLoading(new File(templatePath));
            Template  temp = configuration.getTemplate(templateFileName);
            out = new OutputStreamWriter(new FileOutputStream(targetFilePath), Charset.forName("UTF-8"));
            temp.process(model, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
