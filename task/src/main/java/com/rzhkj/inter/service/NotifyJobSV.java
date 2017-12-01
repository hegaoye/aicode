/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */

package com.rzhkj.inter.service;

/**
 * 消息提醒接口
 * Created by lixin on 2017/8/19.
 */
public interface NotifyJobSV {

    /**
     * 上课时间提醒，大概在上课前30分钟定时进行提醒
     */
    void reminderClassroom();
}
