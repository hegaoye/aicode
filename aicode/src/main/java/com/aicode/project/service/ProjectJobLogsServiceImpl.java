/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.dao.ProjectJobLogsDAO;
import com.aicode.project.dao.mapper.ProjectJobLogsMapper;
import com.aicode.project.entity.ProjectJobLogs;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 任务日志
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectJobLogsServiceImpl extends ServiceImpl<ProjectJobLogsMapper, ProjectJobLogs> implements ProjectJobLogsService {

    @Autowired
    private ProjectJobLogsDAO projectJobLogsDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectJobLogs entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }


}


