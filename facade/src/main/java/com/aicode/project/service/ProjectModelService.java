/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.entity.ProjectModel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 模块
 *
 * @author hegaoye
 */
public interface ProjectModelService extends IService<ProjectModel> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectModel>
     */
    List<ProjectModel> list(QueryWrapper<ProjectModel> queryWrapper, int offset, int limit);
}


