/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.ProjectRepositoryAccount;
import org.springframework.stereotype.Repository;


@Repository
public class ProjectRepositoryAccountDAO extends BaseMybatisDAOImpl<ProjectRepositoryAccount, Long> {

    /**
     * 删除
     *
     * @param code 版本管理编码
     */
    public void delete(String code) {
        getSqlSession().delete(sqlmapNamespace + ".delete", code);
    }

    /**
     * 删除
     *
     */
    public void delete(ProjectRepositoryAccount projectRepositoryAccount) {
        getSqlSession().delete(sqlmapNamespace + ".delete", projectRepositoryAccount);
    }
}
