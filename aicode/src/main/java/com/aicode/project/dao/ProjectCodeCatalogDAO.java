package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectCodeCatalogMapper;
import com.aicode.project.entity.ProjectCodeCatalog;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectCodeCatalog DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectCodeCatalogDAO extends BaseDAO<ProjectCodeCatalogMapper, ProjectCodeCatalog> {


    /**
     * ProjectCodeCatalog mapper
     */
    @Autowired
    private ProjectCodeCatalogMapper projectCodeCatalogMapper;


}