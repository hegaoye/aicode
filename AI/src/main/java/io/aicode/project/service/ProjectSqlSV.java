/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.service;

import io.aicode.core.base.BaseMybatisSV;
import io.aicode.project.entity.ProjectSql;


public interface ProjectSqlSV extends BaseMybatisSV<ProjectSql, Long> {

    /**
     * 删除项目sql
     *
     * @param code tsql编码
     */
    void delete(String code);
}
