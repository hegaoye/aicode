/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import com.google.common.collect.Maps;
import io.aicode.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.MapFieldColumn;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class MapFieldColumnDAO extends BaseMybatisDAOImpl<MapFieldColumn, Long> {


    public void delete(String mapClassTableCode) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("mapClassTableCode", mapClassTableCode);
        getSqlSession().delete(sqlmapNamespace + ".delete", map);
    }

    public List<MapFieldColumn> listFields(String mapClassTableCode) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("mapClassTableCode", mapClassTableCode);
        return getSqlSession().selectList(sqlmapNamespace + ".listFields", params);
    }

}
