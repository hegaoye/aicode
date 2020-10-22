package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectModuleMapper;
import com.aicode.project.entity.ProjectModule;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectModule DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectModuleDAO extends BaseDAO<ProjectModuleMapper, ProjectModule> {


    /**
     * ProjectModule mapper
     */
    @Autowired
    private ProjectModuleMapper projectModuleMapper;


}