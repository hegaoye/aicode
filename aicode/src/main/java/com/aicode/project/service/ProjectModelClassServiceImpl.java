/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.project.dao.ProjectModelClassDAO;
import com.aicode.project.dao.mapper.ProjectModelClassMapper;
import com.aicode.project.entity.ProjectModelClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


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

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectModelClass>
     */
    @Override
    public List<ProjectModelClass> list(QueryWrapper<ProjectModelClass> queryWrapper, int offset, int limit) {
        return projectModelClassDAO.list(queryWrapper, offset, limit);
    }
}


