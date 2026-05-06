/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.dao.ProjectCodeCatalogDAO;
import com.aicode.project.dao.mapper.ProjectCodeCatalogMapper;
import com.aicode.project.entity.ProjectCodeCatalog;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 生成源码资料
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectCodeCatalogServiceImpl extends ServiceImpl<ProjectCodeCatalogMapper, ProjectCodeCatalog> implements ProjectCodeCatalogService {

    @Autowired
    private ProjectCodeCatalogDAO projectCodeCatalogDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectCodeCatalog entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }


}


