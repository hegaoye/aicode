package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectFramworkMapper;
import com.aicode.project.entity.ProjectFramwork;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectFramwork DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectFramworkDAO extends BaseDAO<ProjectFramworkMapper, ProjectFramwork> {


    /**
     * ProjectFramwork mapper
     */
    @Autowired
    private ProjectFramworkMapper projectFramworkMapper;


}