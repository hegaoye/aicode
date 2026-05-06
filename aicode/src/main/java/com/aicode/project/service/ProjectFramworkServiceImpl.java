/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.dao.ProjectFramworkDAO;
import com.aicode.project.dao.mapper.ProjectFramworkMapper;
import com.aicode.project.entity.ProjectFramwork;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 项目应用技术
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectFramworkServiceImpl extends ServiceImpl<ProjectFramworkMapper, ProjectFramwork> implements ProjectFramworkService {

    @Autowired
    private ProjectFramworkDAO projectFramworkDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectFramwork entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }


}


