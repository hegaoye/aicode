/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */

package com.rzhkj.inter.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.rzhkj.setting.service.SettingSV;
import org.springframework.stereotype.Service;

/**
 * 消息提醒接口
 * Created by lixin on 2017/8/19.
 */
@Service
public class NotifyJobSVImpl implements NotifyJobSV {


    @Reference
    SettingSV settingSV;

    /**
     * 上课时间提醒
     * 1.查询所有老师
     * 2.查询老师课程
     * 3.发送即将上课通知
     */
    @Override
    public void reminderClassroom() {

    }
}
