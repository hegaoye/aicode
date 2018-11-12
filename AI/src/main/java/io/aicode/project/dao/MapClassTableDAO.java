/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import com.google.common.collect.Maps;
import io.aicode.core.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.MapClassTable;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class MapClassTableDAO extends BaseMybatisDAOImpl<MapClassTable, Long> {


    public void delete(String mapClassTableCode) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", mapClassTableCode);
        getSqlSession().delete(sqlmapNamespace + ".delete", map);
    }

    public List<MapClassTable> queryByProductCode(String projectCode) {
        Map<String, Object> params = new HashedMap();
        params.put("projectCode", projectCode);
        return getSqlSession().selectList(sqlmapNamespace + ".queryByProductCode", params);
    }
}
