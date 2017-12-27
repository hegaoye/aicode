/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.Frameworks;
import org.springframework.stereotype.Repository;


@Repository
public class FrameworksDAO extends BaseMybatisDAOImpl<Frameworks, Long> {

    /**
     * 删除
     *
     * @param code 技术编码
     */
    public void delete(String code) {
        getSqlSession().delete(sqlmapNamespace + ".delete", code);
    }
}
