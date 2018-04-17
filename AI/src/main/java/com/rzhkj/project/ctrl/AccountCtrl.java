package com.rzhkj.project.ctrl;

import com.rzhkj.core.base.BaseCtrl;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.tools.DateTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


/**
 * iot
 *
 * @author lixin
 */
@Controller
@RequestMapping("/iot")
@Api(value = "iot", description = "iot")
public class AccountCtrl extends BaseCtrl {


    /**
     * 开关
     *
     * @param code
     * @return
     */
    @ApiOperation(value = "开", notes = "开")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "指令编码", required = true, paramType = "query")
    })
    @GetMapping("/on")
    @ResponseBody
    public BeanRet on(String code) {
        logger.info("on::" + code);
        return BeanRet.create(true, DateTools.yyyyMMddHHmmss(new Date()) + " on::" + code);
    }

    /**
     * 关闭
     *
     * @param code
     * @return
     */
    @ApiOperation(value = "关", notes = "关")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "指令编码", required = true, paramType = "query")
    })
    @GetMapping("/off")
    @ResponseBody
    public BeanRet off(String code) {
        logger.info("off::" + code);
        return BeanRet.create(true, DateTools.yyyyMMddHHmmss(new Date()) + " off::" + code);
    }
}
