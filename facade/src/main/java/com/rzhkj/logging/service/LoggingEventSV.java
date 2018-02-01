/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于AI-Code.目.
 */


package com.rzhkj.logging.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.logging.entity.LoggingEvent;

import java.util.Date;
import java.util.List;

/**
 * 日志查询接口
 * 1.查询日志列表
 * 2.查询日志详情
 * 3.关键词搜索日志
 * 4.根据日期范围搜索日志
 */
public interface LoggingEventSV extends BaseMybatisSV<LoggingEvent, Long> {


    /**
     * 查询日志详情
     *
     * @param eventId 日志id
     * @return 日志对象
     */
    LoggingEvent loadLoggingEvent(Long eventId);

    /**
     * 查询日志列表
     *
     * @param offset 查询开始行
     * @param limit  查询行数
     * @return
     */
    List<LoggingEvent> listLoggingEvent(int offset, int limit);

    int countLoggingEvent();

    /**
     * 根据日期范围搜索日志
     *
     * @param createtimeBegin 开始时间
     * @param createtimeEnd   结束时间
     * @param keyword         关键词
     * @param offset          查询开始行
     * @param limit           查询行数
     * @return
     */
    List<LoggingEvent> listLoggingEvent(Date createtimeBegin, Date createtimeEnd, String keyword, int offset, int limit);

    int countLoggingEvent(Date createtimeBegin, Date createtimeEnd, String keyword);

    /**
     * 关键词搜索日志
     *
     * @param keyword 关键词
     * @param offset  查询开始行
     * @param limit   查询行数
     * @return
     */
    List<LoggingEvent> listLoggingEvent(String keyword, int offset, int limit);

    int countLoggingEvent(String keyword);


}
