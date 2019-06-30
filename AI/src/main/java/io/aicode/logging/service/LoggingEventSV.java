/*
 *
 *                        http://www.aicode.io
 *
 *
 *       本代码仅用于AI-Code.目.
 */


package io.aicode.logging.service;

import io.aicode.base.BaseMybatisSV;
import io.aicode.logging.entity.LoggingEvent;

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
