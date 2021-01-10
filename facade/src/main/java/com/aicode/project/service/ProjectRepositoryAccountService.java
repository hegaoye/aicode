/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.entity.ProjectRepositoryAccount;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 版本控制管理
 *
 * @author hegaoye
 */
public interface ProjectRepositoryAccountService extends IService<ProjectRepositoryAccount> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectRepositoryAccount>
     */
    List<ProjectRepositoryAccount> list(QueryWrapper<ProjectRepositoryAccount> queryWrapper, int offset, int limit);
}


