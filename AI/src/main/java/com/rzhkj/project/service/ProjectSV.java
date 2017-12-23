/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;


import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.Project;

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

    /**
     * 加工数据
     *
     * @param code 项目编码
     */
    void process(String code);
}
