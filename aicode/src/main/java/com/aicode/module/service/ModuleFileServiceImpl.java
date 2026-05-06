/*
 * aicode
 */
package com.aicode.module.service;

import com.aicode.module.dao.ModuleFileDAO;
import com.aicode.module.dao.mapper.ModuleFileMapper;
import com.aicode.module.entity.ModuleFile;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 模块文件信息
 *
 * @author aicode
 */
@Slf4j
@Service
public class ModuleFileServiceImpl extends ServiceImpl<ModuleFileMapper, ModuleFile> implements ModuleFileService {

    @Autowired
    private ModuleFileDAO moduleFileDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Transactional
    @Override
    public boolean save(ModuleFile entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }

}


