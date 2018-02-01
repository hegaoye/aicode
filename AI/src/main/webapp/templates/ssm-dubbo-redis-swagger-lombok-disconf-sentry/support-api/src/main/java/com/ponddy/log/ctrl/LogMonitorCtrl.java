package ${basePackage}.log.ctrl;

import com.alibaba.dubbo.config.annotation.Reference;
import ${basePackage}.core.base.BaseCtrl;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.entity.Page;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.logging.entity.LoggingEvent;
import ${basePackage}.logging.service.LoggingEventSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lixin on 2017/6/5.
 */
@Controller
@RequestMapping("/log")
@Api(value = "日志监控控制器", description = "日志监控控制器，控制显示系统日志信息用于系统维护")
public class LogMonitorCtrl extends BaseCtrl {
    private final static Logger logger = LoggerFactory.getLogger(LogMonitorCtrl.class);

    @Reference
    private LoggingEventSV loggingEventSV;

    /**
     * 查询日志详情
     *
     * @param eventId 日志id号
     * @return 日志详情
     */
    @ApiOperation(value = "查询日志详情", notes = "查询日志详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eventId", value = "日志id号", required = true, paramType = "query")
    })
    @RequestMapping(value = "/logdetail", method = RequestMethod.GET)
    @ResponseBody
    public BeanRet logdetail(String eventId) {
        try {
            Assert.hasText(eventId, BaseException.BaseExceptionEnum.Empty_Param.toString());
            LoggingEvent loggingEvent = loggingEventSV.loadLoggingEvent(Long.parseLong(eventId));
            return BeanRet.create(true, "", loggingEvent);
        } catch (Exception e) {
            return BeanRet.create(e.getMessage());
        }
    }

    /**
     * 查询日志列表
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @ApiOperation(value = "查询日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @RequestMapping(value = "/loglist", method = RequestMethod.GET)
    @ResponseBody
    public BeanRet logs(@ApiIgnore Page<LoggingEvent> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            List<LoggingEvent> loggingEvents = loggingEventSV.listLoggingEvent(page.genRowStart(), page.getPageSize());
            int total = loggingEventSV.countLoggingEvent();

            page.setVoList(loggingEvents);
            page.setTotalRow(total);
            return BeanRet.create(true, "", page);
        } catch (Exception e) {
            return BeanRet.create(e.getMessage());
        }
    }


    /**
     * 根据关键词搜索日志
     *
     * @param keyword 关键词
     * @param page    分页对象
     * @return 分页对象
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键词", required = true, paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @ApiOperation(value = "根据关键词搜索日志")
    @RequestMapping(value = "/searchloglist", method = RequestMethod.GET)
    @ResponseBody
    public BeanRet searchlogs(String keyword, @ApiIgnore Page page) {
        try {
            Assert.hasText(keyword, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            List<LoggingEvent> loggingEvents = loggingEventSV.listLoggingEvent(keyword, page.genRowStart(), page.getPageSize());
            int total = loggingEventSV.countLoggingEvent(keyword);

            page.setVoList(loggingEvents);
            page.setTotalRow(total);
            return BeanRet.create(true, "", page);
        } catch (Exception e) {
            return BeanRet.create(e.getMessage());
        }
    }


    /**
     * 根据关时间范围，关键词搜索日志
     *
     * @param keyword   关键词
     * @param timeBegin 开始时间
     * @param timeEnd   结束时间
     * @param page      分页对象
     * @return 分页对象
     */
    @ApiOperation(value = "根据关时间范围，关键词搜索日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键词", required = true, paramType = "query"),
            @ApiImplicitParam(name = "timeBegin", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "timeEnd", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @RequestMapping(value = "/logtimelist", method = RequestMethod.GET)
    @ResponseBody
    public BeanRet logs(String keyword, String timeBegin, String timeEnd, @ApiIgnore Page<LoggingEvent> page) {
        try {
            Assert.hasText(keyword, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(timeBegin, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(timeEnd, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Date startDate = DateFormat.getDateInstance().parse(timeBegin);
            Date endDate = DateFormat.getDateInstance().parse(timeEnd);

            List<LoggingEvent> loggingEvents = loggingEventSV.listLoggingEvent(startDate, endDate, keyword, page.genRowStart(), page.getPageSize());
            int total = loggingEventSV.countLoggingEvent(startDate, endDate, keyword);

            page.setVoList(loggingEvents);
            page.setTotalRow(total);
            return BeanRet.create(true, "", page);
        } catch (Exception e) {
            return BeanRet.create(e.getMessage());
        }
    }


}
