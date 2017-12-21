/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.Database;
import org.springframework.stereotype.Repository;


@Repository
public class DatabaseDAO extends BaseMybatisDAOImpl<Database, Long> {
    /**
     * 创建数据库
     *
     * @param sql             sql
     * @param defaultDatabase 默认数据库
     */
    public void createDatabase(String sql, String defaultDatabase) {
        getSqlSession().selectOne(sqlmapNamespace + ".createDatabase", sql);
        getSqlSession().selectOne(sqlmapNamespace + ".useDatabase", defaultDatabase);
    }
}
