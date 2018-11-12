/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service;


import io.aicode.core.base.BaseMybatisSV;
import io.aicode.project.entity.Project;

public interface ProjectSV extends BaseMybatisSV<Project, Long> {


    /**
     * 创建项目
     *
     * @param project 项目对象
     */
    void build(Project project);

    /**
     * 删除项目
     *
     * @param code 项目编码
     */
    void delete(String code);

    /**
     * 执行脚本
     *
     * @param code 项目编码
     */
    void execute(String code);

}
