/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import com.google.common.collect.Maps;
import io.aicode.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.MapRelationship;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class MapRelationshipDAO extends BaseMybatisDAOImpl<MapRelationship, Long> {


    public void delete(String code) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        getSqlSession().delete(sqlmapNamespace + ".delete", map);
    }

    /**
     * 根据项目编码统计是否已经设置表关系
     *
     * @param projectCode 项目编码
     * @return int
     */
    public int countByProjectCode(String projectCode) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("projectCode", projectCode);
        return getSqlSession().selectOne(sqlmapNamespace + ".countByProjectCode", map);
    }
}
