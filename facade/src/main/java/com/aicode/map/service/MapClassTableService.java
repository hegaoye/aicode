/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.service;

import com.aicode.map.entity.MapClassTable;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 类表映射信息
 *
 * @author hegaoye
 */
public interface MapClassTableService extends IService<MapClassTable> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<MapClassTable>
     */
    List<MapClassTable> list(QueryWrapper<MapClassTable> queryWrapper, int offset, int limit);
}


