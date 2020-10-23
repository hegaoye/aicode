/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.entity.ProjectJob;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 任务
 *
 * @author hegaoye
 */
public interface ProjectJobService extends IService<ProjectJob> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectJob>
     */
    List<ProjectJob> list(QueryWrapper<ProjectJob> queryWrapper, int offset, int limit);

    /**
     * 执行任务
     *
     * @param projectCode 任务编码
     */
    ProjectJob execute(String projectCode);
}


