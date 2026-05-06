/*
 * aicode
 */
package com.aicode.frameworks.service;

import com.aicode.frameworks.dao.FrameworksTemplateDAO;
import com.aicode.frameworks.dao.mapper.FrameworksTemplateMapper;
import com.aicode.frameworks.entity.FrameworksTemplate;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 框架配置文件模板
 *
 * @author aicode
 */
@Slf4j
@Service
public class FrameworksTemplateServiceImpl extends ServiceImpl<FrameworksTemplateMapper, FrameworksTemplate> implements FrameworksTemplateService {

    @Autowired
    private FrameworksTemplateDAO frameworksTemplateDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Transactional
    @Override
    public boolean save(FrameworksTemplate entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }

}


