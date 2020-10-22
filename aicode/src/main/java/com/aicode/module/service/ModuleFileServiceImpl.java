/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.module.dao.ModuleFileDAO;
import com.aicode.module.dao.mapper.ModuleFileMapper;
import com.aicode.module.entity.ModuleFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


/**
 * 模块文件信息
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ModuleFileServiceImpl extends ServiceImpl<ModuleFileMapper, ModuleFile> implements ModuleFileService {

    @Autowired
    private ModuleFileDAO moduleFileDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ModuleFile entity) {
//        entity.setId(String.valueOf(uidGenerator.getUID()));
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ModuleFile>
     */
    @Override
    public List<ModuleFile> list(QueryWrapper<ModuleFile> queryWrapper, int offset, int limit) {
        return moduleFileDAO.list(queryWrapper, offset, limit);
    }
}


