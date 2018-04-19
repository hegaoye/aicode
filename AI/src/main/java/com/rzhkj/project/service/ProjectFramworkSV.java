/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.project.entity.ProjectFramwork;

import java.util.List;

public interface ProjectFramworkSV extends BaseMybatisSV<ProjectFramwork, Long> {


    /**
     * 批量存储 项目框架选择
     *
     * @param projectFramwors 项目选择的框架集合
     */
    void save(List<ProjectFramwork> projectFramwors);

    /**
     * 删除
     *
     * @param projectFramwork 项目技术框架对象
     */
    void delete(ProjectFramwork projectFramwork);
}
