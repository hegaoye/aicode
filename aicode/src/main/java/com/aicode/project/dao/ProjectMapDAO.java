package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectMapMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectMap DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectMapDAO {


    /**
     * ProjectMap mapper
     */
    @Autowired
    private ProjectMapMapper projectMapMapper;


}