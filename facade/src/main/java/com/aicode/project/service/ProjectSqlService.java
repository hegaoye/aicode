/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.entity.ProjectSql;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 项目sql脚本
 *
 * @author hegaoye
 */
public interface ProjectSqlService extends IService<ProjectSql> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectSql>
     */
    List<ProjectSql> list(QueryWrapper<ProjectSql> queryWrapper, int offset, int limit);
}


