/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.service;

import com.aicode.module.entity.Module;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 第三方模块池
 *
 * @author hegaoye
 */
public interface ModuleService extends IService<Module> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Module>
     */
    List<Module> list(QueryWrapper<Module> queryWrapper, int offset, int limit);
}


