/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.ProjectMap;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class ProjectMapDAO extends BaseMybatisDAOImpl<ProjectMap, Long> {


    public void delete(Map<String, Object> map) {
        getSqlSession().delete(sqlmapNamespace + ".delete", map);
    }
}
