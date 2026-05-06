package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectJobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectJob DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectJobDAO {


    /**
     * ProjectJob mapper
     */
    @Autowired
    private ProjectJobMapper projectJobMapper;


}