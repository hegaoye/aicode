/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.ProjectFramwork;

public interface ProjectFramworkSV extends BaseMybatisSV<ProjectFramwork, Long> {

    /**
     * 删除
     *
     * @param projectFramwork 项目技术框架对象
     */
    void delete(ProjectFramwork projectFramwork);
}
