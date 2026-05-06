package com.aicode.map.dao;

import com.aicode.map.dao.mapper.MapRelationshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * MapRelationship DAO
 * 数据服务层
 *
 * @author aicode
 */
@Repository
public class MapRelationshipDAO {


    /**
     * MapRelationship mapper
     */
    @Autowired
    private MapRelationshipMapper mapRelationshipMapper;

    public int countByProjectCode(String code) {
        return mapRelationshipMapper.countByProjectCode(code);
    }

}