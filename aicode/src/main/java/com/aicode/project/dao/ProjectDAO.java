package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Project DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectDAO {


    /**
     * Project mapper
     */
    @Autowired
    private ProjectMapper projectMapper;


}