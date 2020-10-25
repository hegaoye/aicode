package com.aicode.config.template;

import com.aicode.project.entity.TemplateData;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by chendehui on 20/3/8.
 */

@Slf4j
@Service
public class BeetlHelper implements TemplateHelper {

    @Autowired
    private GroupTemplate groupTemplate;

    /**
     * 模板引擎接口，其余 helper自动实现此接口，进行模板的生成自动切换
     *
     * @param templateData   模板数据对象
     * @param targetFilePath 目标文件路径
     * @param templatePath   模板路径
     */
    @Override
    public String generate(TemplateData templateData, String targetFilePath, String templatePath) {
        try {
            templatePath = templatePath.replace("//", "/").replace("\\", "/");
            targetFilePath = targetFilePath.replace("\\", "/").replace("//", "/");
            String filePath = targetFilePath.substring(0, targetFilePath.lastIndexOf("/"));

            File dirFile = new File(filePath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            FileResourceLoader resourceLoader = new FileResourceLoader("/", "UTF-8");
            groupTemplate.setResourceLoader(resourceLoader);
            Template template = groupTemplate.getTemplate(templatePath);
            Map<String, Object> dataMap = JSON.parseObject(JSON.toJSONString(templateData), Map.class);
            dataMap.put("package", templateData.getBasePackage());
            template.binding(dataMap);
            Writer out = new OutputStreamWriter(new FileOutputStream(targetFilePath), Charset.forName("UTF-8"));
            template.renderTo(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "success";
    }

}
