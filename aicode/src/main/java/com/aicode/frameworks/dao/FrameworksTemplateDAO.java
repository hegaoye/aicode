package com.aicode.frameworks.dao;

import com.aicode.frameworks.dao.mapper.FrameworksTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * FrameworksTemplate DAO
 * 数据服务层
 *
 * @author aicode
 */
@Repository
public class FrameworksTemplateDAO {


    /**
     * FrameworksTemplate mapper
     */
    @Autowired
    private FrameworksTemplateMapper frameworksTemplateMapper;


}