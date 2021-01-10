/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.entity.ProjectCodeCatalog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 生成源码资料
 *
 * @author hegaoye
 */
public interface ProjectCodeCatalogService extends IService<ProjectCodeCatalog> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectCodeCatalog>
     */
    List<ProjectCodeCatalog> list(QueryWrapper<ProjectCodeCatalog> queryWrapper, int offset, int limit);
}


