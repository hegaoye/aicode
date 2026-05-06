/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.dao.ProjectModelDAO;
import com.aicode.project.dao.mapper.ProjectModelMapper;
import com.aicode.project.entity.ProjectModel;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 模块
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectModelServiceImpl extends ServiceImpl<ProjectModelMapper, ProjectModel> implements ProjectModelService {

    @Autowired
    private ProjectModelDAO projectModelDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectModel entity) {
        return super.save(entity);
    }


}


