/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import io.aicode.core.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.FrameworksTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;


@Repository
public class FrameworksTemplateDAO extends BaseMybatisDAOImpl<FrameworksTemplate, Long> {

    public void deleteAll() {
        getSqlSession().delete(sqlmapNamespace + ".delete", new HashMap<>());
    }

}
