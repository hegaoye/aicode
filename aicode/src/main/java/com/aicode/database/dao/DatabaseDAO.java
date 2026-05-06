package com.aicode.database.dao;

import com.aicode.database.dao.mapper.DatabaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DisplayAttribute DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Slf4j
@Repository
public class DatabaseDAO {


    @Autowired
    private DatabaseMapper databaseMapper;


    public long count(String database) {
        return databaseMapper.count(database);
    }

    public void createDatabase(String database, String sql, String defaultDatabase) {
        try {
            //创建数据库
            databaseMapper.createDatabase(database);
            //切换数据库
            databaseMapper.useDatabase(database);
            //创建表
            databaseMapper.createTable(sql);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            try {
                databaseMapper.dropDatabase(database);
            } catch (Exception err) {
                log.error("{}", err.getMessage());
            }
        } finally {
            //无论执行成功或失败，始终切换到默认数据库
            databaseMapper.useDatabase(defaultDatabase);
        }
    }
}