package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectModelMapper;
import com.aicode.project.entity.ProjectModel;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectModel DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectModelDAO extends BaseDAO<ProjectModelMapper, ProjectModel> {


    /**
     * ProjectModel mapper
     */
    @Autowired
    private ProjectModelMapper projectModelMapper;


}