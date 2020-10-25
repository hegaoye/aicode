package com.aicode.frameworks.dao;

import com.aicode.frameworks.dao.mapper.FrameworksTemplateMapper;
import com.aicode.frameworks.entity.FrameworksTemplate;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * FrameworksTemplate DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class FrameworksTemplateDAO extends BaseDAO<FrameworksTemplateMapper, FrameworksTemplate> {


    /**
     * FrameworksTemplate mapper
     */
    @Autowired
    private FrameworksTemplateMapper frameworksTemplateMapper;


}