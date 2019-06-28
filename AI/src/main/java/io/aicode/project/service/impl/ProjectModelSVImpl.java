/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.project.service.impl;

import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.project.dao.ProjectModelDAO;
import io.aicode.project.entity.ProjectModel;
import io.aicode.project.service.ProjectModelSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 模块
 *
 * @author lixin
 */
@Component
@Service
public class ProjectModelSVImpl extends BaseMybatisSVImpl<ProjectModel, Long> implements ProjectModelSV {


    @Resource
    private ProjectModelDAO projectModelDAO;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectModelDAO;
    }


    /**
     * 加载对象模块 通过code
     *
     * @param code 模块编码
     * @return ProjectModel
     */
    @Override
    public ProjectModel loadByCode(String code) {
        return projectModelDAO.loadByCode(code);
    }

    /**
     * 删除对象模块
     *
     * @param id * @param code 模块编码
     */
    @Override
    public void delete(Long id, String code) {
        projectModelDAO.delete(id, code);
    }


}