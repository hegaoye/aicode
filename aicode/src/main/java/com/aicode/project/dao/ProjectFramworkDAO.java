package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectFramworkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectFramwork DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectFramworkDAO {


    /**
     * ProjectFramwork mapper
     */
    @Autowired
    private ProjectFramworkMapper projectFramworkMapper;


}