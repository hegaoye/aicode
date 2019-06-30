/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import com.beust.jcommander.internal.Maps;
import io.aicode.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.Frameworks;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class FrameworksDAO extends BaseMybatisDAOImpl<Frameworks, Long> {

    /**
     * 删除
     *
     * @param code 技术编码
     */
    public void delete(String code) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("code", code);
        getSqlSession().delete(sqlmapNamespace + ".delete", params);
    }
}
