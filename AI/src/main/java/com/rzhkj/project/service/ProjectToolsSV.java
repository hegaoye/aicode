/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.ProjectTools;

public interface ProjectToolsSV extends BaseMybatisSV<ProjectTools, Long> {

    /**
     * 删除
     *
     * @param projectTools 项目工具对象
     */
    void delete(ProjectTools projectTools);
}
