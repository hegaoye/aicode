/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.map.dao.MapRelationshipDAO;
import com.aicode.map.dao.mapper.MapRelationshipMapper;
import com.aicode.map.entity.MapRelationship;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


/**
 * 模型关系
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class MapRelationshipServiceImpl extends ServiceImpl<MapRelationshipMapper, MapRelationship> implements MapRelationshipService {

    @Autowired
    private MapRelationshipDAO mapRelationshipDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(MapRelationship entity) {
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<MapRelationship>
     */
    @Override
    public List<MapRelationship> list(QueryWrapper<MapRelationship> queryWrapper, int offset, int limit) {
        return mapRelationshipDAO.list(queryWrapper, offset, limit);
    }

    @Override
    public int countByProjectCode(String code) {
        return mapRelationshipDAO.countByProjectCode(code);
    }
}


