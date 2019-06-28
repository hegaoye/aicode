/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.project.service;

import io.aicode.core.base.BaseMybatisSV;
import io.aicode.project.entity.ProjectModel;

/**
 * 模块
 *
 * @author lixin
 */
public interface ProjectModelSV extends BaseMybatisSV<ProjectModel, Long> {

    /**
     * 加载对象模块 通过code
     *
     * @param code 模块编码
     * @return ProjectModel
     */
    ProjectModel loadByCode(String code);


    /**
     * 删除对象模块
     *
     * @param id @param code 模块编码
     * @return ProjectModel
     */
    void delete(Long id, String code);


}
