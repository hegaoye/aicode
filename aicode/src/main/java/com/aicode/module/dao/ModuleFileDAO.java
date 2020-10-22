package com.aicode.module.dao;

import com.aicode.module.dao.mapper.ModuleFileMapper;
import com.aicode.module.entity.ModuleFile;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ModuleFile DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ModuleFileDAO extends BaseDAO<ModuleFileMapper, ModuleFile> {


    /**
     * ModuleFile mapper
     */
    @Autowired
    private ModuleFileMapper moduleFileMapper;


}