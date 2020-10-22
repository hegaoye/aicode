package com.aicode.map.dao;

import com.aicode.map.dao.mapper.MapFieldColumnMapper;
import com.aicode.map.entity.MapFieldColumn;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * MapFieldColumn DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class MapFieldColumnDAO extends BaseDAO<MapFieldColumnMapper, MapFieldColumn> {


    /**
     * MapFieldColumn mapper
     */
    @Autowired
    private MapFieldColumnMapper mapFieldColumnMapper;


}