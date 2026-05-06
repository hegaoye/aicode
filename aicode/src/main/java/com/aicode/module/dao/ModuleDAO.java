package com.aicode.module.dao;

import com.aicode.module.dao.mapper.ModuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Module DAO
 * 数据服务层
 *
 * @author aicode
 */
@Repository
public class ModuleDAO {


    /**
     * Module mapper
     */
    @Autowired
    private ModuleMapper moduleMapper;


}