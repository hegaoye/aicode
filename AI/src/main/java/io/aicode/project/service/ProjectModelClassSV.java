/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.project.service;

import io.aicode.core.base.BaseMybatisSV;
import io.aicode.project.entity.ProjectModelClass;

/**
 * 模块下的类
 *
 * @author lixin
 */
public interface ProjectModelClassSV extends BaseMybatisSV<ProjectModelClass, Long> {

    /**
     * 加载对象模块下的类 通过mapClassTableCode
     *
     * @param mapClassTableCode 类编码
     * @return ProjectModelClass
     */
    ProjectModelClass loadByMapClassTableCode(String mapClassTableCode);

    /**
     * 加载对象模块下的类 通过projectModelCode
     *
     * @param projectModelCode 模块编码
     * @return ProjectModelClass
     */
    ProjectModelClass loadByProjectModelCode(String projectModelCode);


    /**
     * 删除对象模块下的类
     *
     * @param id @param mapClassTableCode 类编码@param projectModelCode 模块编码
     * @return ProjectModelClass
     */
    void delete(Long id, String mapClassTableCode, String projectModelCode);


}
