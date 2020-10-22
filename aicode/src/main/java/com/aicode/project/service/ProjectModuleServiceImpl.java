/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.project.dao.ProjectModuleDAO;
import com.aicode.project.dao.mapper.ProjectModuleMapper;
import com.aicode.project.entity.ProjectModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


/**
 * 项目选择模块
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectModuleServiceImpl extends ServiceImpl<ProjectModuleMapper, ProjectModule> implements ProjectModuleService {

    @Autowired
    private ProjectModuleDAO projectModuleDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectModule entity) {
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectModule>
     */
    @Override
    public List<ProjectModule> list(QueryWrapper<ProjectModule> queryWrapper, int offset, int limit) {
        return projectModuleDAO.list(queryWrapper, offset, limit);
    }
}


