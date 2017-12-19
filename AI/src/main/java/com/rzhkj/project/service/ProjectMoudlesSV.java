/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.ProjectMoudles;

public interface ProjectMoudlesSV extends BaseMybatisSV<ProjectMoudles, Long> {

    /**
     * 删除
     *
     * @param projectMoudles 项目选择的模块
     */
    void delete(ProjectMoudles projectMoudles);
}
