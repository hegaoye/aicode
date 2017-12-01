/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于龐帝業務系统.
 */

package com.rzhkj.base.core;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 抽象作业Job类（非必要不要扩展此类），
 * 如果某个类需要实现定时作业功能，
 * 只需要继承此类并重写抽象方法完成业务即可，
 * 其余由框架自动完成
 * <p>
 * 特别注意子类凡继承并实现作业的需要保持统一命名规则XXXJob的格式 例如TestJob
 *
 * @author lixin on 2017/1/22 0022.
 */
public abstract class JobNative implements Job {
    /**
     * 重写JOb的执行方法（非必要不要改动此处业务）
     * 1.作业执行前
     * 2.执行作业
     * 3.作业执行后
     */
    @Override
    public final void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //1.作业执行前
        boolean flag = preExecute(jobExecutionContext);
        if (!flag) return;
        //2.执行作业
        execute();
        //3.作业执行后
        afterExecute(jobExecutionContext);
    }

    /**
     * 执行前的操作
     * 只允许子类继承重写此方法，不重写自动调用默认业务
     *
     * @param context 作业全局对象
     * @return true/false 返回true时继续执行，返回false时自动终止执行其后的方法
     */
    protected boolean preExecute(JobExecutionContext context) {
        return true;
    }

    /**
     * job执行的作业方法
     * 具体执行逻辑交由子类实现，不实现此方法作业无法进行
     */
    protected abstract void execute();

    /**
     * 执行后的操作
     * 只允许子类继承重写此方法，不重写自动调用默认业务
     *
     * @param context 作业全局对象
     */
    protected void afterExecute(JobExecutionContext context) {
    }


}
