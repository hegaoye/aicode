/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.service;

import com.aicode.map.entity.MapRelationship;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 模型关系
 *
 * @author hegaoye
 */
public interface MapRelationshipService extends IService<MapRelationship> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<MapRelationship>
     */
    List<MapRelationship> list(QueryWrapper<MapRelationship> queryWrapper, int offset, int limit);

    int countByProjectCode(String code);
}


