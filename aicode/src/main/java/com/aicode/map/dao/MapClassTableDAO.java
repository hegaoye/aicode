package com.aicode.map.dao;

import com.aicode.map.dao.mapper.MapClassTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * MapClassTable DAO
 * 数据服务层
 *
 * @author aicode
 */
@Repository
public class MapClassTableDAO {


    /**
     * MapClassTable mapper
     */
    @Autowired
    private MapClassTableMapper mapClassTableMapper;


}