/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.service;

import com.aicode.frameworks.entity.Frameworks;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 框架技术池
 *
 * @author hegaoye
 */
public interface FrameworksService extends IService<Frameworks> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Frameworks>
     */
    List<Frameworks> list(QueryWrapper<Frameworks> queryWrapper, int offset, int limit);
}


