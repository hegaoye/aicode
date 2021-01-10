package com.aicode.database.dao;

import com.aicode.core.base.BaseDAO;
import com.aicode.database.dao.mapper.DatabaseMapper;
import com.aicode.database.entity.Database;
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
public class DatabaseDAO extends BaseDAO<DatabaseMapper, Database> {


    @Autowired
    private DatabaseMapper databaseMapper;


    public long count(String database) {
        return databaseMapper.count(database);
    }

    public void createDatabase(String sql, String defaultDatabase) {
        try {
            databaseMapper.createDatabase(sql);
        } finally {
            //无论执行成功或失败，始终切换到默认数据库
            databaseMapper.useDatabase(defaultDatabase);
        }
    }
}