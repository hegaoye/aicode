/*
 * aicode
 */
package com.aicode.module.service;

import com.aicode.module.dao.ModuleDAO;
import com.aicode.module.dao.mapper.ModuleMapper;
import com.aicode.module.entity.Module;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 第三方模块池
 *
 * @author aicode
 */
@Slf4j
@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> implements ModuleService {

    @Autowired
    private ModuleDAO moduleDAO;
    @Transactional
    @Override
    public boolean save(Module entity) {
        return super.save(entity);
    }

}


