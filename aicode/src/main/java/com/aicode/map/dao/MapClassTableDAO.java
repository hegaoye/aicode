package com.aicode.map.dao;

import com.aicode.map.dao.mapper.MapClassTableMapper;
import com.aicode.map.entity.MapClassTable;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * MapClassTable DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class MapClassTableDAO extends BaseDAO<MapClassTableMapper, MapClassTable> {


    /**
     * MapClassTable mapper
     */
    @Autowired
    private MapClassTableMapper mapClassTableMapper;


}