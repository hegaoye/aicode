/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.FrameworksTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;


@Repository
public class FrameworksTemplateDAO extends BaseMybatisDAOImpl<FrameworksTemplate, Long> {

    public void deleteAll() {
        getSqlSession().delete(sqlmapNamespace + ".delete", new HashMap<>());
    }

}
