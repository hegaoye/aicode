package com.aicode.module.dao;

import com.aicode.module.dao.mapper.ModuleFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ModuleFile DAO
 * 数据服务层
 *
 * @author aicode
 */
@Repository
public class ModuleFileDAO {


    /**
     * ModuleFile mapper
     */
    @Autowired
    private ModuleFileMapper moduleFileMapper;


}