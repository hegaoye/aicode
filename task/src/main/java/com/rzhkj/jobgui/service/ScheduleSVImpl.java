/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于龐帝業務系统.
 */

package com.rzhkj.jobgui.service;

import com.rzhkj.base.ex.JobException;
import com.rzhkj.base.ex.ScheduleException;
import com.rzhkj.jobgui.dao.QrtzJobDetailsDao;
import com.rzhkj.jobgui.dao.QrtzTriggersDao;
import com.rzhkj.jobgui.entity.QrtzJobDetails;
import com.rzhkj.jobgui.entity.QrtzTriggers;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

/**
 * @author lixin on 2017/1/22 0022.
 */
@Service("scheduleSV")
public class ScheduleSVImpl implements ScheduleSV {
    @Resource
    private QrtzTriggersDao qrtzTriggersDao;//作业触发器dao

    @Resource
    private QrtzJobDetailsDao qrtzJobDetailsDao;//作业dao

    @Resource
    private Scheduler scheduler;// 作业调度器

    /**
     * 查询作业信息集合
     *
     * @return 作业信息集合
     */
    @Override
    public List<QrtzTriggers> listTriggers(int startRow, int rowSize) {
        return qrtzTriggersDao.listTriggers(startRow, rowSize);
    }

    @Override
    public int countTriggers() {
        return qrtzTriggersDao.countTriggers();
    }


    /**
     * 查询作业信息集合
     *
     * @param startRow 开始条数
     * @param rowSize  限制条数
     * @return 作业信息集合
     */
    @Override
    public List<QrtzJobDetails> listJobDetails(int startRow, int rowSize) {
        return qrtzJobDetailsDao.listJobDetails(startRow, rowSize);
    }

    @Override
    public int countJobDetails() {
        return qrtzJobDetailsDao.countJobDetails();
    }

    /**
     * 新增作业
     *
     * @param jobName        作业名称
     * @param jobGroup       作业和触发器所在分组
     * @param jobClassName   作业的类名称，必须是全限定类名 如：com.xxx.service（不能是接口）
     * @param triggerName    触发器名称
     * @param cronExpression 时间规则 例如：* * * * * ?
     * @param description    描述
     * @throws ScheduleException 调度器异常
     */
    @Override
    public void buildScheduleJob(String jobName, String jobGroup, String jobClassName,
                                 String triggerName, String cronExpression, String description) throws ScheduleException {
        try {
            JobDetailImpl jobDetail = new JobDetailImpl(jobName, jobGroup, (Class<? extends Job>) Class.forName(jobClassName));
            jobDetail.setDescription(description);
            CronTriggerImpl cronTrigger = new CronTriggerImpl(triggerName, jobGroup);
            cronTrigger.setDescription(description);
            cronTrigger.setCronExpression(cronExpression);
            scheduler.scheduleJob(jobDetail, cronTrigger);
        } catch (SchedulerException e) {
            throw new ScheduleException("新增作业发生异常", e);
        } catch (ParseException e) {
            throw new ScheduleException("新增作业发生异常", e);
        } catch (ClassNotFoundException e) {
            throw new ScheduleException("新增作业发生异常", e);
        }
    }

    /**
     * 变更作业触发器的时间规则
     *
     * @param triggerName    触发器名称
     * @param triggerGroup   触发器分组
     * @param description   触发器描述
     * @param cronExpression 时间规则 例如：* * * * * ?
     * @throws ScheduleException 调度器异常
     */
    @Override
    public void reScheduleJob(String triggerName, String triggerGroup, String cronExpression,String description) throws ScheduleException {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            CronTriggerImpl newTrigger = new CronTriggerImpl(triggerName, triggerGroup);
            newTrigger.setCronExpression(cronExpression);
            newTrigger.setDescription(description);
            scheduler.rescheduleJob(triggerKey, newTrigger);
        } catch (SchedulerException e) {
            throw new ScheduleException("变更作业触发器的时间规则时发生异常", e);
        } catch (ParseException e) {
            throw new ScheduleException("变更作业触发器的时间规则时发生异常", e);
        }
    }


    /**
     * 执行作业，不考虑时间规则
     *
     * @param jobName  作业名称
     * @param jobGroup 分组
     * @throws ScheduleException 调度器异常
     */
    @Override
    public void triggerJob(String jobName, String jobGroup) throws ScheduleException {
        try {
            JobKey jobKey = new JobKey(jobName, jobGroup);
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            throw new ScheduleException("执行作业时发生异常", e);
        }
    }

    /**
     * 暂停作业
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @throws ScheduleException 调度器异常
     */
    @Override
    public void pauseTrigger(String triggerName, String triggerGroup) throws ScheduleException {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            scheduler.pauseTrigger(triggerKey);
        } catch (SchedulerException e) {
            throw new ScheduleException("暂停作业是发生异常", e);
        }
    }

    /**
     * 暂停作业
     *
     * @param jobName  作业名称
     * @param jobGroup 作业组
     * @throws JobException
     */
    @Override
    public void pauseJob(String jobName, String jobGroup) throws JobException {
        try {
            JobKey jobKey = new JobKey(jobName, jobGroup);
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启作业
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @throws ScheduleException 调度器异常
     */
    @Override
    public void resumeTrigger(String triggerName, String triggerGroup) throws ScheduleException {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            scheduler.resumeTrigger(triggerKey);
        } catch (SchedulerException e) {
            throw new ScheduleException("重启作业时发生异常", e);
        }
    }

    /**
     * 移除作业触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return boolean true/false
     * @throws ScheduleException 调度器异常
     */
    @Override
    public boolean removeTrigger(String triggerName, String triggerGroup) throws ScheduleException {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            return scheduler.unscheduleJob(triggerKey);// 移除触发器
        } catch (SchedulerException e) {
            throw new ScheduleException("删除作业时发生异常", e);
        }
    }

    /**
     * 加载一条作业详情
     *
     * @param jobName 作业名称
     * @return 作业对象
     */
    @Override
    public QrtzJobDetails loadJobDetail(String jobName) {
        if (jobName == null || jobName == "") throw new JobException("作业名称为空异常");
        QrtzJobDetails qrtzJobDetails = qrtzJobDetailsDao.loadJobDetail(jobName);
        if (qrtzJobDetails == null) return null;
        return qrtzJobDetails;
    }
}
