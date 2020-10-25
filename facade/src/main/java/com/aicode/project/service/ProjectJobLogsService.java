/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.entity.ProjectJobLogs;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 任务日志
 *
 * @author hegaoye
 */
public interface ProjectJobLogsService extends IService<ProjectJobLogs> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectJobLogs>
     */
    List<ProjectJobLogs> list(QueryWrapper<ProjectJobLogs> queryWrapper, int offset, int limit);
}


