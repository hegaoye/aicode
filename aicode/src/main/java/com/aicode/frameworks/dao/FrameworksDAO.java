package com.aicode.frameworks.dao;

import com.aicode.frameworks.dao.mapper.FrameworksMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Frameworks DAO
 * 数据服务层
 *
 * @author aicode
 */
@Repository
public class FrameworksDAO {


    /**
     * Frameworks mapper
     */
    @Autowired
    private FrameworksMapper frameworksMapper;


}