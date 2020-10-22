/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.display.service;

import com.aicode.display.entity.DisplayAttribute;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 显示属性
 *
 * @author hegaoye
 */
public interface DisplayAttributeService extends IService<DisplayAttribute> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<DisplayAttribute>
     */
    List<DisplayAttribute> list(QueryWrapper<DisplayAttribute> queryWrapper, int offset, int limit);
}


