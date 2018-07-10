/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.google.common.collect.Maps;
import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.Project;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class ProjectDAO extends BaseMybatisDAOImpl<Project, Long> {

    public void update(String projectCode, int i) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", projectCode);
        map.put("buildNumber", i);
        getSqlSession().update(sqlmapNamespace + ".update", map);
    }


    /**
     * 删除
     *
     * @param project
     */
    public void delete(Project project) {
        getSqlSession().delete(sqlmapNamespace + ".delete", project);
    }
}
