/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import com.google.common.collect.Maps;
import io.aicode.core.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.MapRelationship;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class MapRelationshipDAO extends BaseMybatisDAOImpl<MapRelationship, Long> {


    public void delete(String mapClassTableCode) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("mapClassTableCode", mapClassTableCode);
        getSqlSession().delete(sqlmapNamespace + ".delete", map);
    }
}
