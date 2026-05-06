/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.dao.ProjectModelClassDAO;
import com.aicode.project.dao.mapper.ProjectModelClassMapper;
import com.aicode.project.entity.ProjectModelClass;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 模块下的类
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectModelClassServiceImpl extends ServiceImpl<ProjectModelClassMapper, ProjectModelClass> implements ProjectModelClassService {

    @Autowired
    private ProjectModelClassDAO projectModelClassDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectModelClass entity) {
        return super.save(entity);
    }


}


