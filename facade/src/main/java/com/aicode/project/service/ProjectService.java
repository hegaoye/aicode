/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.entity.Project;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 项目信息
 *
 * @author hegaoye
 */
public interface ProjectService extends IService<Project> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Project>
     */
    List<Project> list(QueryWrapper<Project> queryWrapper, int offset, int limit);

    /**
     * 删除项目
     * 1.判断项目是否存在
     * 2.删除数据
     * 3.删除项目
     *
     * @param code 项目编码
     */
    void delete(String code);

    void execute(String code);
}


