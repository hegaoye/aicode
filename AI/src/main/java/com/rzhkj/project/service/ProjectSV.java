/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;


import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.Project;

public interface ProjectSV extends BaseMybatisSV<Project, Long> {

    /**
     * 创建数据库
     *
     * @param projectCode 项目编码
     * @return true/false
     */
    boolean createDatabase(String projectCode);
}
