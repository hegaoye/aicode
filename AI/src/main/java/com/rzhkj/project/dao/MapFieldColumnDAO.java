/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.google.common.collect.Maps;
import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.MapFieldColumn;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class MapFieldColumnDAO extends BaseMybatisDAOImpl<MapFieldColumn, Long> {


    public void delete(String mapClassTableCode) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("mapClassTableCode", mapClassTableCode);
        getSqlSession().delete(sqlmapNamespace + ".delete", map);
    }
}
