package com.rzhkj.inter.job;

import com.rzhkj.base.core.JobNative;
import com.rzhkj.inter.service.NotifyJobSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * 界面管理配置此类即可自动完成作业
 * 上课时间提醒
 * com.ponddy.inter.job.NotifyJob
 * 每25分钟执行1次
 * 0 0/25 * * * ?
 *
 * @author lixin
 */
public class NotifyJob extends JobNative {
    private final static Logger logger = LoggerFactory.getLogger(NotifyJob.class);

    @Resource
    private NotifyJobSV notifyJobSV;

    /**
     * 上课时间提醒
     */
    @Override
    protected void execute() {
        try {
            notifyJobSV.reminderClassroom();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
