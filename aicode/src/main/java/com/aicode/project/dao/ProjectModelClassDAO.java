package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectModelClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectModelClass DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectModelClassDAO {


    /**
     * ProjectModelClass mapper
     */
    @Autowired
    private ProjectModelClassMapper projectModelClassMapper;


}