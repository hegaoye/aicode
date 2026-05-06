package com.aicode.database.dao;

import com.aicode.database.dao.mapper.TableMapper;
import com.aicode.database.entity.Table;
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
public class TableDAO {


    /**
     * DisplayAttribute mapper
     */
    @Autowired
    private TableMapper tableMapper;


    public List<Table> list(String database) {
        return tableMapper.list(database);
    }
}