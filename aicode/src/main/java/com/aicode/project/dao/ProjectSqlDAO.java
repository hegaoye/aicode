package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectSqlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectSql DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectSqlDAO {


    /**
     * ProjectSql mapper
     */
    @Autowired
    private ProjectSqlMapper projectSqlMapper;


}