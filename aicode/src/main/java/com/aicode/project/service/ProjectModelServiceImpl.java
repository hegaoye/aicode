/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.project.dao.ProjectModelDAO;
import com.aicode.project.dao.mapper.ProjectModelMapper;
import com.aicode.project.entity.ProjectModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


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

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectModel>
     */
    @Override
    public List<ProjectModel> list(QueryWrapper<ProjectModel> queryWrapper, int offset, int limit) {
        return projectModelDAO.list(queryWrapper, offset, limit);
    }
}


