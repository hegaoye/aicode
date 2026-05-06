package com.aicode.map.dao;

import com.aicode.map.dao.mapper.MapFieldColumnMapper;
import com.aicode.map.entity.MapFieldColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MapFieldColumn DAO
 * 数据服务层
 *
 * @author aicode
 */
@Repository
public class MapFieldColumnDAO {


    /**
     * MapFieldColumn mapper
     */
    @Autowired
    private MapFieldColumnMapper mapFieldColumnMapper;

    public void batchInsert(List<MapFieldColumn> mapFieldColumns) {
        mapFieldColumnMapper.batchInsert(mapFieldColumns);
    }

}