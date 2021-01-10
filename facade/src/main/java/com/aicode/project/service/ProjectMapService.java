/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.entity.ProjectMap;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 项目数据表
 *
 * @author hegaoye
 */
public interface ProjectMapService extends IService<ProjectMap> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectMap>
     */
    List<ProjectMap> list(QueryWrapper<ProjectMap> queryWrapper, int offset, int limit);
}


