package ${basePackage}.setting.ctrl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Maps;
import ${basePackage}.core.base.BaseUploadCtrl;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.tools.StringTools;
import ${basePackage}.setting.entity.Setting;
import ${basePackage}.setting.service.SettingSV;
import ${basePackage}.setting.vo.SettingQuery;
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

import java.util.List;
import java.util.Map;

/**
 * 系统中的参数设置
 * Created by lixin on 2017/6/3.
 */
@Controller
@RequestMapping("/setting")
@Api(value = "系统中的参数设置", description = "系统中的参数设置接口")
public class SettingCtrl extends BaseUploadCtrl {
    private final static Logger logger = LoggerFactory.getLogger(SettingCtrl.class);

    @Reference
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
    @GetMapping("/loadset")
    @ResponseBody
    public BeanRet loadSetting(String k) {
        logger.info("加载一个系统中的参数设置 k : ====> " + k);
        Assert.hasText(k, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Map<String, Object> map = Maps.newHashMap();
        map.put("k", k);
        Setting setting = settingSV.load(map);
        return BeanRet.create(true, "Load Setting info Success!", setting.getV());
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
            @ApiImplicitParam(name = "summary", value = "说明", paramType = "query")
    })
    @PostMapping("/modifyset")
    @ResponseBody
    public BeanRet modifySetting(@ApiIgnore Setting setting) {
        Assert.notNull(setting, BaseException.BaseExceptionEnum.Empty_Param.toString());
        settingSV.modifySetting(setting);
        return BeanRet.create(true, "Modify Success!");
    }


    /**
     * 查询所有设置参数
     *
     * @return
     */
    @ApiOperation(value = "查询所有设置参数")
    @GetMapping(value = "/settings")
    @ResponseBody
    public BeanRet settingList() {
        List<Setting> settings = settingSV.listSetting();
        return BeanRet.create(true, "", StringTools.convertList(settings, SettingQuery.class));
    }


    /**
     * 查询所有设置参数
     *
     * @return
     */
    @ApiOperation(value = "查询所有设置参数")
    @GetMapping(value = "/setting/custom/upperlimit")
    @ResponseBody
    public BeanRet settingCustomUpperlimit() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("k", Setting.Key.Custom_Upper_Limit.name());
        Setting setting = settingSV.load(map);
        return BeanRet.create(true, "", setting.getV());
    }
}
