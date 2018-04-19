/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.google.common.collect.Maps;
import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.MapClassTable;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class MapClassTableDAO extends BaseMybatisDAOImpl<MapClassTable,Long> {


    public void delete(String mapClassTableCode) {
        Map<String,Object> map= Maps.newHashMap();
        map.put("code",mapClassTableCode);
        getSqlSession().delete(sqlmapNamespace+".delete",map);
    }
}
