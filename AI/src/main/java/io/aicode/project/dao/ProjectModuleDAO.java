/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.dao;

import io.aicode.core.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.ProjectModule;
import org.springframework.stereotype.Repository;


@Repository
public class ProjectModuleDAO extends BaseMybatisDAOImpl<ProjectModule,Long> {

    /**
     * 删除
     *
     */
    public void delete(ProjectModule projectModule) {
        getSqlSession().delete(sqlmapNamespace + ".delete", projectModule);
    }

}
