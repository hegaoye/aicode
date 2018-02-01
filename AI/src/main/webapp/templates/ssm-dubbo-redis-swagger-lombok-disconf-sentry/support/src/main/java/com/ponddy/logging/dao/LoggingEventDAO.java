/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于龐帝業務系统.目.
 */



package ${basePackage}.logging.dao;

import ${basePackage}.core.base.BaseMybatisDAOImpl;
import ${basePackage}.logging.entity.LoggingEvent;
import com.google.common.collect.Maps;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class LoggingEventDAO extends BaseMybatisDAOImpl<LoggingEvent, Long> {

    //查询日志列表
    public List<LoggingEvent> listLoggingEvent(RowBounds rowBounds) {
        return getSqlSession().selectList(sqlmapNamespace + ".listLoggingEvent", rowBounds);
    }

    public int countLoggingEvent() {
        return getSqlSession().selectOne(sqlmapNamespace + ".countLoggingEvent");
    }

    //关键词搜索日志
    public List<LoggingEvent> listLoggingEvent(String keyword, RowBounds rowBounds) {
        return getSqlSession().selectList(sqlmapNamespace + ".listLoggingEventForSearch", keyword, rowBounds);
    }

    public int countLoggingEvent(String keyword) {
        return getSqlSession().selectOne(sqlmapNamespace + ".countLoggingEventForSearch", keyword);
    }

    //根据日期范围搜索日志
    public List<LoggingEvent> listLoggingEvent(long createtimeBegin, long createtimeEnd, String keyword, RowBounds rowBounds) {
        Map param = Maps.newHashMap();
        param.put("createtimeBegin", createtimeBegin);
        param.put("createtimeEnd", createtimeEnd);
        param.put("keyword", keyword);
        return getSqlSession().selectList(sqlmapNamespace + ".listLoggingEventByTime", param, rowBounds);
    }

    public int countLoggingEvent(long createtimeBegin, long createtimeEnd, String keyword) {
        Map param = Maps.newHashMap();
        param.put("createtimeBegin", createtimeBegin);
        param.put("createtimeEnd", createtimeEnd);
        param.put("keyword", keyword);
        return getSqlSession().selectOne(sqlmapNamespace + ".countLoggingEventByTime", param);
    }
}
