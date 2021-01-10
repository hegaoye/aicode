/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.service;

import com.aicode.map.entity.MapFieldColumn;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 字段属性映射信息
 *
 * @author hegaoye
 */
public interface MapFieldColumnService extends IService<MapFieldColumn> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<MapFieldColumn>
     */
    List<MapFieldColumn> list(QueryWrapper<MapFieldColumn> queryWrapper, int offset, int limit);
}


