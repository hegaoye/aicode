/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import io.aicode.core.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.Database;
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
