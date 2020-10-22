/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.project.dao.ProjectJobDAO;
import com.aicode.project.dao.mapper.ProjectJobMapper;
import com.aicode.project.entity.ProjectJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


/**
 * 任务
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectJobServiceImpl extends ServiceImpl<ProjectJobMapper, ProjectJob> implements ProjectJobService {

    @Autowired
    private ProjectJobDAO projectJobDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectJob entity) {
//        entity.setId(String.valueOf(uidGenerator.getUID()));
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectJob>
     */
    @Override
    public List<ProjectJob> list(QueryWrapper<ProjectJob> queryWrapper, int offset, int limit) {
        return projectJobDAO.list(queryWrapper, offset, limit);
    }
}


