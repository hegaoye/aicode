/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.module.dao.ModuleDAO;
import com.aicode.module.dao.mapper.ModuleMapper;
import com.aicode.module.entity.Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


/**
 * 第三方模块池
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> implements ModuleService {

    @Autowired
    private ModuleDAO moduleDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(Module entity) {
//        entity.setId(String.valueOf(uidGenerator.getUID()));
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Module>
     */
    @Override
    public List<Module> list(QueryWrapper<Module> queryWrapper, int offset, int limit) {
        return moduleDAO.list(queryWrapper, offset, limit);
    }
}


