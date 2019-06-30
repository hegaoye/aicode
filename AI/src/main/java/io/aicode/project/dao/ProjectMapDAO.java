/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import io.aicode.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.ProjectMap;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class ProjectMapDAO extends BaseMybatisDAOImpl<ProjectMap, Long> {


    public void delete(Map<String, Object> map) {
        getSqlSession().delete(sqlmapNamespace + ".delete", map);
    }


}
