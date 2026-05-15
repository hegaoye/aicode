/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.core.BaseException;
import com.aicode.exceptions.ProjectJobException;
import com.aicode.project.dao.mapper.ProjectJobMapper;
import com.aicode.project.entity.ProjectJob;
import com.aicode.project.entity.ProjectJobState;
import com.alibaba.druid.util.StringUtils;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * 任务
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectJobServiceImpl extends ServiceImpl<ProjectJobMapper, ProjectJob> implements ProjectJobService {

    @Autowired
    private ProjectJobMapper projectJobMapper;

    @Autowired
    private UidGenerator uidGenerator;

    @Autowired
    private GenerateSV generateSV;

    @Override
    public boolean save(ProjectJob entity) {
        //1.验证参数
        if (StringUtils.isEmpty(entity.getProjectCode())) {
            log.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectJobException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        //2.设置默认属性
        entity.setCode(String.valueOf(uidGenerator.getUID()));
        entity.setNumber(0);
        entity.setState(ProjectJobState.Create.name());
        entity.setCreateTime(new Date());
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
        queryWrapper.last("limit " + offset + "," + limit);
        return projectJobMapper.selectList(queryWrapper);
    }


    /**
     * 执行任务
     * 1.创建项目
     * 2.获取类信息
     * 3.获取模板信息
     * 4.生成源码
     * 5.获取模块信息
     * 6.获取版本控制管理信息
     *
     * @param projectCode 项目编码
     * @return ProjectJob
     */

    @Override
    public ProjectJob execute(String projectCode) {
        log.info("执行任务, projectCode:{}", projectCode);
        //创建任务追踪
        ProjectJob projectJob = new ProjectJob();
        projectJob.setId(uidGenerator.getUID());
        projectJob.setCode(String.valueOf(uidGenerator.getUID()));
        projectJob.setProjectCode(projectCode);
        projectJob.setState(ProjectJobState.Executing.name());
        projectJob.setNumber(1);
        projectJob.setCreateTime(new Date());
        projectJobMapper.insert(projectJob);

        //执行异步任务
        this.generateCode(projectCode, projectJob);
        //        Executors.cacheThreadExecutor(new Runnable() {
        //            @Override
        //            public void run() {
        //                generateSV.aiCode(projectCode, projectJob);
        //            }
        //        });
        return projectJob;
    }

    @Async
    public void generateCode(String projectCode, ProjectJob projectJob) {
        generateSV.aiCode(projectCode, projectJob);
    }
}


