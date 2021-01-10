/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.service;

import com.aicode.frameworks.entity.FrameworksTemplate;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 框架配置文件模板
 *
 * @author hegaoye
 */
public interface FrameworksTemplateService extends IService<FrameworksTemplate> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<FrameworksTemplate>
     */
    List<FrameworksTemplate> list(QueryWrapper<FrameworksTemplate> queryWrapper, int offset, int limit);
}


