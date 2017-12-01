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
import com.rzhkj.jobgui.entity.QrtzJobDetails;
import com.rzhkj.jobgui.entity.QrtzTriggers;

import java.util.List;

/**
 * 调度作业接口
 *
 * @author lixin on 2017/1/22 0022.
 */
public interface ScheduleSV {

    /**
     * 查询作业触发器信息集合
     *
     * @param startRow 开始条数
     * @param rowSize  限制条数
     * @return 作业触发器信息集合
     */
    List<QrtzTriggers> listTriggers(int startRow, int rowSize);

    int countTriggers();

    /**
     * 查询作业信息集合
     *
     * @param startRow 开始条数
     * @param rowSize  限制条数
     * @return 作业信息集合
     */
    List<QrtzJobDetails> listJobDetails(int startRow, int rowSize);

    int countJobDetails();

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
    void buildScheduleJob(String jobName, String jobGroup, String jobClassName,
                          String triggerName, String cronExpression, String description) throws ScheduleException;


    /**
     * 变更作业触发器的时间规则
     *
     * @param triggerName    触发器名称
     * @param triggerGroup   触发器分组
     * @param description   触发器描述
     * @param cronExpression 时间规则 例如：* * * * * ?
     * @throws ScheduleException 调度器异常
     */
    void reScheduleJob(String triggerName, String triggerGroup, String cronExpression,String description) throws ScheduleException;

    /**
     * 执行作业，不考虑时间规则
     *
     * @param jobName  作业名称
     * @param jobGroup 分组
     * @throws ScheduleException 调度器异常
     */
    void triggerJob(String jobName, String jobGroup) throws ScheduleException;

    /**
     * 暂停作业触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @throws ScheduleException
     */
    void pauseTrigger(String triggerName, String triggerGroup) throws ScheduleException;

    /**
     * 暂停作业
     *
     * @param jobName  作业名称
     * @param jobGroup 作业组
     * @throws JobException
     */
    void pauseJob(String jobName, String jobGroup) throws JobException;

    /**
     * 重启作业
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @throws ScheduleException 调度器异常
     */
    void resumeTrigger(String triggerName, String triggerGroup) throws ScheduleException;

    /**
     * 移除作业触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return boolean true/false
     * @throws ScheduleException 调度器异常
     */
    boolean removeTrigger(String triggerName, String triggerGroup) throws ScheduleException;

    /**
     * 加载一条作业详情
     *
     * @param jobName 作业名称
     * @return 作业对象
     */
    QrtzJobDetails loadJobDetail(String jobName);
}
