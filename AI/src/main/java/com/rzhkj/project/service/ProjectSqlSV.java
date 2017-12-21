/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.ProjectSql;


public interface ProjectSqlSV extends BaseMybatisSV<ProjectSql, Long> {

    /**
     * 删除项目sql
     *
     * @param code tsql编码
     */
    void delete(String code);
}
