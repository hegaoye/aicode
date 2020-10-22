package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectMapper;
import com.aicode.project.entity.Project;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Project DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectDAO extends BaseDAO<ProjectMapper, Project> {


    /**
     * Project mapper
     */
    @Autowired
    private ProjectMapper projectMapper;


}