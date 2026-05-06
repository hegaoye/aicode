/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.dao.ProjectMapDAO;
import com.aicode.project.dao.mapper.ProjectMapMapper;
import com.aicode.project.entity.ProjectMap;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 项目数据表
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectMapServiceImpl extends ServiceImpl<ProjectMapMapper, ProjectMap> implements ProjectMapService {

    @Autowired
    private ProjectMapDAO projectMapDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectMap entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }

}


