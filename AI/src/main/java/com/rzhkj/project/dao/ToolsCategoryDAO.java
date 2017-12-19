/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.ToolsCategory;
import org.springframework.stereotype.Repository;


@Repository
public class ToolsCategoryDAO extends BaseMybatisDAOImpl<ToolsCategory, Long> {


    /**
     * 删除
     *
     * @param code 类型编码
     */
    public void delete(String code) {
        getSqlSession().delete(sqlmapNamespace + ".delete", code);
    }
}
