/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.ProjectJob;

public interface ProjectJobSV extends BaseMybatisSV<ProjectJob, Long> {

    /**
     * 创建项目任务
     *
     * @param projectJob
     */
    void build(ProjectJob projectJob);

    /**
     * 删除任务
     *
     * @param code 任务编码
     */
    void delete(String code);

    /**
     * 执行任务
     *
     * @param code 任务编码
     */
    ProjectJob execute(String code);
}
