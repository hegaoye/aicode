package com.aicode.database.dao;

import com.aicode.database.dao.mapper.ColumMapper;
import com.aicode.database.entity.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DisplayAttribute DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ColumnDAO {


    /**
     * DisplayAttribute mapper
     */
    @Autowired
    private ColumMapper columMapper;


    public List<Column> list(String database, String tableName) {
        return columMapper.list(database, tableName);
    }
}