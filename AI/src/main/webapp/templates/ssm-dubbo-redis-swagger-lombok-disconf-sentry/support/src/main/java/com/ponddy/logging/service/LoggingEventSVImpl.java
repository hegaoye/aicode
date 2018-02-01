/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于龐帝業務系统.目.
 */



package ${basePackage}.logging.service;

import ch.qos.logback.core.LogbackException;
import ${basePackage}.core.base.BaseMybatisDAO;
import ${basePackage}.core.base.BaseMybatisSVImpl;
import ${basePackage}.core.entity.Page;
import ${basePackage}.core.enums.ExceptionMsgEnum;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.logging.dao.LoggingEventDAO;
import ${basePackage}.logging.entity.LoggingEvent;
import com.alibaba.dubbo.config.annotation.Service;
import com.baidu.fsg.uid.UidGenerator;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 日志查询接口
 * 1.查询日志列表
 * 2.查询日志详情
 * 3.关键词搜索日志
 * 4.根据日期范围搜索日志
 */
@Component
@Service
public class LoggingEventSVImpl extends BaseMybatisSVImpl<LoggingEvent, Long> implements LoggingEventSV {
    private final static Logger logger = LoggerFactory.getLogger(LoggingEventSVImpl.class);

    @Resource
    private LoggingEventDAO loggingEventDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return loggingEventDAO;
    }

    /**
     * 查询日志详情
     *
     * @param eventId 日志id
     * @return 日志对象
     */
    @Override
    public LoggingEvent loadLoggingEvent(Long eventId) {
        if (eventId == null || eventId <= 0) {
            logger.error("查询日志详情:" + BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new LogbackException("查询日志详情:" + BaseException.BaseExceptionEnum.Empty_Param.toString());
        }
        return loggingEventDAO.load(eventId);
    }

    /**
     * 查询日志列表
     *
     * @param offset 查询开始行
     * @param limit  查询行数
     * @return
     */
    @Override
    public List<LoggingEvent> listLoggingEvent(int offset, int limit) {
        if (offset < 0) {
            offset = 0;
        }
        if (limit < 0){
            limit = Page.limit;
        }
        return loggingEventDAO.listLoggingEvent(new RowBounds(offset, limit));
    }

    @Override
    public int countLoggingEvent() {
        return loggingEventDAO.countLoggingEvent();
    }

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
    @Override
    public List<LoggingEvent> listLoggingEvent(Date createtimeBegin, Date createtimeEnd, String keyword, int offset, int limit) {
        return loggingEventDAO.listLoggingEvent(createtimeBegin.getTime(), createtimeEnd.getTime(), keyword, new RowBounds(offset, limit));
    }

    @Override
    public int countLoggingEvent(Date createtimeBegin, Date createtimeEnd, String keyword) {
        return loggingEventDAO.countLoggingEvent(createtimeBegin.getTime(), createtimeEnd.getTime(), keyword);
    }

    /**
     * 关键词搜索日志
     *
     * @param keyword 关键词
     * @param offset  查询开始行
     * @param limit   查询行数
     * @return
     */
    @Override
    public List<LoggingEvent> listLoggingEvent(String keyword, int offset, int limit) {
        return loggingEventDAO.listLoggingEvent(keyword, new RowBounds(offset, limit));
    }

    @Override
    public int countLoggingEvent(String keyword) {
        return loggingEventDAO.countLoggingEvent(keyword);
    }
}
