package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectRepositoryAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectRepositoryAccount DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectRepositoryAccountDAO {


    /**
     * ProjectRepositoryAccount mapper
     */
    @Autowired
    private ProjectRepositoryAccountMapper projectRepositoryAccountMapper;


}