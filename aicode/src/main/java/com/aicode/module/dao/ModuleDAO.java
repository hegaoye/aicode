package com.aicode.module.dao;

import com.aicode.module.dao.mapper.ModuleMapper;
import com.aicode.module.entity.Module;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Module DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ModuleDAO extends BaseDAO<ModuleMapper, Module> {


    /**
     * Module mapper
     */
    @Autowired
    private ModuleMapper moduleMapper;


}