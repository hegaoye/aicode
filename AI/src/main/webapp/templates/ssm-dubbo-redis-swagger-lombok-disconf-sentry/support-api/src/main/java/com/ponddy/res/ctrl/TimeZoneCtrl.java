/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */

package ${basePackage}.res.ctrl;

import com.alibaba.dubbo.config.annotation.Reference;
import ${basePackage}.area.entity.TimeZone;
import ${basePackage}.area.service.TimeZoneSV;
import ${basePackage}.area.vo.TimeZoneQuery;
import ${basePackage}.core.base.BaseCtrl;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.tools.JSON;
import ${basePackage}.core.tools.StringTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 时区管理
 * Created by lixin on 2017/6/9.
 */
@Controller
@RequestMapping("/res/tz")
@Api(value = "时区管理", description = "时区管理")
public class TimeZoneCtrl extends BaseCtrl {
    private final static Logger logger = LoggerFactory.getLogger(TimeZoneCtrl.class);

    @Reference
    private TimeZoneSV timeZoneSV;


    /**
     * 获得所有时区信息
     */
    @ApiOperation(value = "获得所有时区信息", notes = "获得所有时区信息")
    @GetMapping("/timezones")
    @ResponseBody
    public BeanRet listProvoiceAll() {
        List<TimeZone> timeZones = timeZoneSV.queryList(null);
        logger.info(" 获得所有时区信息 : " + JSON.toJSONString(timeZones));
        return BeanRet.create(true, "", StringTools.convertList(timeZones, TimeZoneQuery.class));
    }

}
