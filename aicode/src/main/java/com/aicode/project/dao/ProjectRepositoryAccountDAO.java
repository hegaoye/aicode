package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectRepositoryAccountMapper;
import com.aicode.project.entity.ProjectRepositoryAccount;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectRepositoryAccount DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectRepositoryAccountDAO extends BaseDAO<ProjectRepositoryAccountMapper, ProjectRepositoryAccount> {


    /**
     * ProjectRepositoryAccount mapper
     */
    @Autowired
    private ProjectRepositoryAccountMapper projectRepositoryAccountMapper;


}