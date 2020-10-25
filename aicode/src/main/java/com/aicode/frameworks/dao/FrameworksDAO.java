package com.aicode.frameworks.dao;

import com.aicode.frameworks.dao.mapper.FrameworksMapper;
import com.aicode.frameworks.entity.Frameworks;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Frameworks DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class FrameworksDAO extends BaseDAO<FrameworksMapper, Frameworks> {


    /**
     * Frameworks mapper
     */
    @Autowired
    private FrameworksMapper frameworksMapper;


}