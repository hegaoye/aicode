/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于龐帝業務系统.
 */

package com.rzhkj.base;

import com.alibaba.fastjson.JSON;
import com.rzhkj.base.core.JobNative;
import com.rzhkj.jobgui.service.ScheduleSV;

import javax.annotation.Resource;

/**
 * 特别注意子类凡继承并实现作业,
 * 需要保持统一命名规则XXXJob的格式
 * 例如CheckJob
 *
 * @author lixin on 2017/1/23 0023.
 */
public class CheckJob extends JobNative {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CheckJob.class);

    @Resource
    private ScheduleSV scheduleSV;

    @Override
    protected void execute() {
        log.info("this is test job!!!,it works!!!");
        log.info(JSON.toJSONString(scheduleSV.listTriggers(0, 100)));
    }
}
