package com.aicode.core.tools.core.template;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.aicode.base.core.TemplateData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

@Slf4j
@Service
public class FreemarkerHelper implements TemplateHelper {
    public static void main(String[] args) throws IOException, TemplateException {
        Map<String, Object> map = Maps.newHashMap();
        map.put("fileName", "Hegaoye");
        map.put("className", "public\tclass\t" + map.get("fileName") + "\t");
        map.put("extends", "extends\tPage\timplements\tSerializable\t");
        map.put("basePackage", "package\tcom.szh.test.ctrl;");
        map.put("import", "import\tcom.aixin.core.entity.Page;\nimport\torg.apache.commons.lang.builder.ToStringBuilder;\nimport\torg.apache.commons.lang.builder.ToStringStyle;\nimport\tjava.io.Serializable;");
        String targetFilePath = "C:\\worspaces\\template\\src\\main\\webapp\\workspace\\szh\\sv\\src\\main\\java\\com\\szh\\test\\ctrl\\" + map.get("fileName").toString() + ".java";
        new FreemarkerHelper().generate(null, targetFilePath, "C:\\worspaces\\template\\AI\\src\\main\\webapp\\templates\\Test.java");
    }


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
            Map<String, Object> param = JSON.parseObject(JSON.toJSONString(templateData), Map.class);
            param.put("package", templateData.getBasePackage());
            temp.process(param, out);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (TemplateException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "success";
    }
}
