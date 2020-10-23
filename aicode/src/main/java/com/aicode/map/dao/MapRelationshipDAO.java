package com.aicode.map.dao;

import com.aicode.map.dao.mapper.MapRelationshipMapper;
import com.aicode.map.entity.MapRelationship;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * MapRelationship DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class MapRelationshipDAO extends BaseDAO<MapRelationshipMapper, MapRelationship> {


    /**
     * MapRelationship mapper
     */
    @Autowired
    private MapRelationshipMapper mapRelationshipMapper;


    public int countByProjectCode(String code) {
        return mapRelationshipMapper.countByProjectCode(code);
    }
}