/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */

package io.aicode.setting.ctrl;

import com.google.common.collect.Maps;
import io.aicode.base.BaseCtrl;
import io.aicode.base.core.BeanRet;
import io.aicode.base.exceptions.BaseException;
import io.aicode.setting.entity.Setting;
import io.aicode.setting.service.SettingSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 系统中的参数设置
 * Created by lixin on 2017/6/3.
 */
@Controller
@RequestMapping("/setting")
@Api(value = "系统中的参数设置", description = "系统中的参数设置接口")
public class SettingCtrl extends BaseCtrl {
    private final static Logger logger = LoggerFactory.getLogger(SettingCtrl.class);

    @Resource
    private SettingSV settingSV;


    /**
     * 加载一个系统中的参数设置
     *
     * @return BeanRet
     */
    @ApiOperation(value = "加载一个系统中的参数设置", notes = "加载一个系统中的参数设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "k", value = "键", required = true, paramType = "query")
    })
    @GetMapping("/load")
    @ResponseBody
    public BeanRet load(String k) {
        try {
            logger.info("加载一个系统中的参数设置 k : ====> " + k);
            Assert.hasText(k, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Map<String, Object> map = Maps.newHashMap();
            map.put("k", k);
            Setting setting = settingSV.load(map);
            return BeanRet.create(true, "Load Setting info Success!", setting.getV());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "Load Setting info failed!");
        }
    }


    /**
     * 修改设置值
     *
     * @param setting 设置对象
     * @return BeanRet
     */
    @ApiOperation(value = "修改设置值", notes = "修改设置值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "k", value = "键", required = true, paramType = "query"),
            @ApiImplicitParam(name = "v", value = "值", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "说明", paramType = "query")
    })
    @PostMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore Setting setting) {
        try {
            Assert.notNull(setting, BaseException.BaseExceptionEnum.Empty_Param.toString());
            settingSV.modifySetting(setting);
            return BeanRet.create(true, "Modify Success!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "Modify failed!");
        }
    }


    /**
     * 查询所有设置参数
     *
     * @return
     */
    @ApiOperation(value = "查询所有设置参数")
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list() {
        try {
            List<Setting> settings = settingSV.listSetting();
            return BeanRet.create(true, "", settings);
        } catch (Exception e) {
            return BeanRet.create(e.getMessage());
        }
    }

}
