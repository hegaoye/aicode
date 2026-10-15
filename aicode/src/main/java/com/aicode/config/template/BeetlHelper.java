package com.aicode.config.template;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;
import com.alibaba.fastjson2.JSON;

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

            String text = FileUtil.readUtf8String(templatePath);
            Template template = groupTemplate.getTemplate(text);

            // 保留 fastjson2 的 bean -> String -> Map 往返，SHALL NOT 换成 hutool BeanUtil.beanToMap：
            // fastjson2 按 **getter** 命名产出键（basePackage / className / dashedCaseName），
            // 而 hutool 按 **字段** 产出键（base_package / classname ...），两者键集不一致，
            // 换过去会让所有外部模板仓的 ${className} 等变量取不到值 —— 生成产物静默改变。
            // 回归守护见 TemplateDataExpansionTest。
            Map<String, Object> dataMap = JSON.parseObject(JSON.toJSONString(templateData), Map.class);
            dataMap.put("package", templateData.getBasePackage());
            template.binding(dataMap);
            Writer out = new OutputStreamWriter(new FileOutputStream(targetFilePath), Charset.forName("UTF-8"));
            template.renderTo(out);
        } catch (FileNotFoundException e) {
            log.error("异常", e);
            return e.getMessage();
        }
        return "success";
    }

}
