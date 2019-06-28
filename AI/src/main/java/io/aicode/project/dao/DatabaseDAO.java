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
        try {
            getSqlSession().selectOne(sqlmapNamespace + ".createDatabase", sql);
        } finally {
            //无论执行成功或失败，始终切换到默认数据库
            getSqlSession().selectOne(sqlmapNamespace + ".useDatabase", defaultDatabase);
        }
    }
}
