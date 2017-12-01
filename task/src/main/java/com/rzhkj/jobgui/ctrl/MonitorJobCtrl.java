/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于龐帝業務系统.
 */

package com.rzhkj.jobgui.ctrl;

import com.rzhkj.base.core.BeanRet;
import com.rzhkj.base.core.DatatablesViewPage;
import com.rzhkj.base.ex.ScheduleException;
import com.rzhkj.core.entity.Page;
import com.rzhkj.jobgui.entity.QrtzJobDetails;
import com.rzhkj.jobgui.entity.QrtzTriggers;
import com.rzhkj.jobgui.service.ScheduleSV;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对作业进行监控
 *
 * @author lixin on 2017/1/22 0022.
 */
@Controller
@RequestMapping("/job/Triggers")
public class MonitorJobCtrl {

    @Resource
    private ScheduleSV scheduleSV;

    /**
     * 查询作业触发器信息集合
     * 1.查询作业触发器集合
     * 2.统计作业触发器总数
     */
    @RequestMapping("/listTriggers")
    @ResponseBody
    public BeanRet listTriggers(Page<QrtzTriggers> page) {
        // 1.查询作业集合
        List<QrtzTriggers> listTriggers = scheduleSV.listTriggers(page.genRowStart(), page.getPageSize());
        page.setVoList(listTriggers);
        //2.统计作业总数
        page.setTotalRow(scheduleSV.countTriggers());
        return returnInfo(page);
    }

    /**
     * 查询作业信息集合
     * 1.查询作业集合
     * 2.统计作业总数
     */
    @RequestMapping("/listJobDetails")
    @ResponseBody
    public BeanRet listJobDetails(Page<QrtzJobDetails> page) {
        // 1.查询作业集合
        List<QrtzJobDetails> listTriggers = scheduleSV.listJobDetails(page.genRowStart(), page.getPageSize());
        page.setVoList(listTriggers);
        //2.统计作业总数
        page.setTotalRow(scheduleSV.countJobDetails());
        return returnInfo(page);
    }


    /**
     * 加载一条作业详情
     *
     * @param jobName 作业名称
     * @return 作业对象
     */
    @RequestMapping("/loadJobDetail")
    @ResponseBody
    public BeanRet loadJobDetail(String jobName) {
        if (StringUtils.isBlank(jobName)) {
            BeanRet.create(false, "作业名称不能为空");
        }
        QrtzJobDetails qrtzJobDetails = scheduleSV.loadJobDetail(jobName);
        return qrtzJobDetails == null ? BeanRet.create(false, "作业不存在") : BeanRet.create(true, "查询成功", qrtzJobDetails);
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
     * @return BeanRet
     */
    @RequestMapping("/buildTrigger")
    @ResponseBody
    public BeanRet buildScheduleJob(String jobName, String jobGroup, String jobClassName,
                                    String triggerName, String cronExpression, String description) {
        try {
            if (StringUtils.isBlank(jobName)) {
                BeanRet.create(false, "作业名称不能为空");
            }
            if (StringUtils.isBlank(jobGroup)) {
                BeanRet.create(false, "作业所在分组不能为空");
            }
            if (StringUtils.isBlank(jobClassName)) {
                BeanRet.create(false, "作业的类名称不能为空");
            }
            if (StringUtils.isBlank(triggerName)) {
                BeanRet.create(false, "触发器名称不能为空");
            }
            if (StringUtils.isBlank(cronExpression)) {
                BeanRet.create(false, "时间规则不能为空");
            }
            if (StringUtils.isBlank(description)) {
                BeanRet.create(false, "描述不能为空");
            }
            scheduleSV.buildScheduleJob(jobName, jobGroup, jobClassName, triggerName, cronExpression, description);
        } catch (ScheduleException e) {
            return BeanRet.create(false, "新增作业时发生异常");
        }
        return BeanRet.create(true, "新增作业完成");
    }

    /**
     * 变更作业触发器的时间规则
     *
     * @param triggerName    触发器名称
     * @param triggerGroup   触发器分组
     * @param cronExpression 时间规则 例如：* * * * * ?
     * @throws ScheduleException 调度器异常
     */
    @RequestMapping("/reBuildTrigger")
    @ResponseBody
    public BeanRet reScheduleJob(String triggerName, String triggerGroup, String cronExpression, String description) {
        try {
            if (StringUtils.isBlank(triggerName)) {
                BeanRet.create(false, "触发器名称不能为空");
            }
            if (StringUtils.isBlank(cronExpression)) {
                BeanRet.create(false, "时间规则不能为空");
            }
            if (StringUtils.isBlank(description)) {
                BeanRet.create(false, "描述不能为空");
            }
            scheduleSV.reScheduleJob(triggerName, triggerGroup, cronExpression, description);
        } catch (ScheduleException e) {
            return BeanRet.create(false, "变更作业触发器的时间规则发生异常");
        }
        return BeanRet.create(true, "变更作业触发器的时间规则完成");
    }


    /**
     * 执行作业，不考虑时间规则
     *
     * @param jobName  触发器名称
     * @param jobGroup 触发器分组
     * @return BeanRet
     */
    @RequestMapping("/exeTrigger")
    @ResponseBody
    public BeanRet exeTrigger(String jobName, String jobGroup) {
        try {
            if (StringUtils.isBlank(jobName)) {
                BeanRet.create(false, "作业名称不能为空");
            }
            if (StringUtils.isBlank(jobGroup)) {
                BeanRet.create(false, "作业所在分组不能为空");
            }

            scheduleSV.triggerJob(jobName, jobGroup);
        } catch (ScheduleException e) {
            return BeanRet.create(false, "执行作业时发生异常");
        }
        return BeanRet.create(true, "执行作业完成");
    }

    /**
     * 暂停作业触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return BeanRet
     */
    @RequestMapping("/stopTrigger")
    @ResponseBody
    public BeanRet pauseTrigger(String triggerName, String triggerGroup) {
        try {
            if (StringUtils.isBlank(triggerName)) {
                BeanRet.create(false, "触发器名称不能为空");
            }
            if (StringUtils.isBlank(triggerGroup)) {
                BeanRet.create(false, "触发器分组不能为空");
            }
            scheduleSV.pauseTrigger(triggerName, triggerGroup);
        } catch (ScheduleException e) {
            return BeanRet.create(false, "暂停作业时发生异常");
        }
        return BeanRet.create(true, "暂停作业完成");
    }


    /**
     * 暂停作业
     *
     * @param jobName  作业名称
     * @param jobGroup 作业分组
     * @return BeanRet
     */
    @RequestMapping("/stopJob")
    @ResponseBody
    public BeanRet pauseJob(String jobName, String jobGroup) {
        try {
            if (StringUtils.isBlank(jobName)) {
                BeanRet.create(false, "作业名称不能为空");
            }
            if (StringUtils.isBlank(jobGroup)) {
                BeanRet.create(false, "作业所在分组不能为空");
            }
            scheduleSV.pauseJob(jobName, jobGroup);
        } catch (ScheduleException e) {
            return BeanRet.create(false, "暂停作业时发生异常");
        }
        return BeanRet.create(true, "暂停作业完成");
    }


    /**
     * 重启作业
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return BeanRet
     */
    @RequestMapping("/restartTrigger")
    @ResponseBody
    public BeanRet restartTrigger(String triggerName, String triggerGroup) {
        try {
            if (StringUtils.isBlank(triggerGroup)) {
                BeanRet.create(false, "作业所在分组不能为空");
            }
            if (StringUtils.isBlank(triggerName)) {
                BeanRet.create(false, "触发器名称不能为空");
            }
            scheduleSV.resumeTrigger(triggerName, triggerGroup);
        } catch (Exception e) {
            return BeanRet.create(false, "重启作业时发生异常");
        }
        return BeanRet.create(true, "重启作业完成");
    }

    /**
     * 移除作业触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return boolean true/false
     * @throws ScheduleException 调度器异常
     */
    @RequestMapping("/removeTrigger")
    @ResponseBody
    public BeanRet removeTrigger(String triggerName, String triggerGroup) {
        try {
            if (StringUtils.isBlank(triggerGroup)) {
                BeanRet.create(false, "作业所在分组不能为空");
            }
            if (StringUtils.isBlank(triggerName)) {
                BeanRet.create(false, "触发器名称不能为空");
            }
            scheduleSV.removeTrigger(triggerName, triggerGroup);
        } catch (Exception e) {
            return BeanRet.create(false, "移除作业触发器时发生异常");
        }
        return BeanRet.create(true, "移除作业触发器完成");
    }


    /**
     * @param page
     * @return
     * @author 立坤 更新于2016.07.18
     * @remark 封装分页返回数据
     */
    public BeanRet returnInfo(Page page) {
        BeanRet bsr = new BeanRet();
        DatatablesViewPage dvp = new DatatablesViewPage();
        dvp.setiTotalDisplayRecords(page.getTotalRow());
        dvp.setiTotalRecords(page.getTotalRow());
        dvp.setAaData(page.getVoList());
        bsr.setData(dvp);
        bsr.setSuccess(true);
        return bsr;
    }

}
