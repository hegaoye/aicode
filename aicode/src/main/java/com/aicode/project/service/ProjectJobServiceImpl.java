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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ProjectJobExecutor projectJobExecutor;

    @Override
    public boolean save(ProjectJob entity) {
        //1.验证参数
        if (StringUtils.isEmpty(entity.getProjectCode())) {
            log.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectJobException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        //2.设置默认属性
        long id = uidGenerator.getUID();
        entity.setId(id);
        entity.setCode(String.valueOf(id));
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
     * 触发一次构建任务。
     * <p>本方法在 HTTP 请求线程内同步执行：参数校验 + 检查并发 + 创建 ProjectJob 追踪记录后，
     * 立即把实际工作交由 {@link ProjectJobExecutor} 异步执行并返回任务对象。</p>
     * <p>并发控制：以 {@code ProjectJob.state} 为锁。
     * 同一 projectCode 已有 Executing 任务时拒绝本次 execute，
     * 异步任务完成（Completed / Error）后自动释放。</p>
     *
     * @param projectCode 项目编码
     * @return 新建的 ProjectJob（state=Executing）
     */
    @Override
    public ProjectJob execute(String projectCode) {
        log.info("执行任务, projectCode:{}", projectCode);

        //并发检查：同一 projectCode 已有 Executing 任务则拒绝
        Long activeCount = projectJobMapper.selectCount(new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getProjectCode, projectCode)
                .eq(ProjectJob::getState, ProjectJobState.Executing.name()));
        if (activeCount != null && activeCount > 0) {
            log.warn("项目已有构建在进行, projectCode:{}", projectCode);
            throw new ProjectJobException(BaseException.BaseExceptionEnum.Server_Error);
        }

        //创建任务追踪
        ProjectJob projectJob = new ProjectJob();
        long id = uidGenerator.getUID();
        projectJob.setId(id);
        projectJob.setCode(String.valueOf(id));
        projectJob.setProjectCode(projectCode);
        projectJob.setState(ProjectJobState.Executing.name());
        projectJob.setNumber(1);
        projectJob.setCreateTime(new Date());
        projectJobMapper.insert(projectJob);

        //交由独立 Bean 执行：避免 this. 自调绕过 Spring AOP 代理
        projectJobExecutor.execute(projectCode, projectJob);
        return projectJob;
    }
}
