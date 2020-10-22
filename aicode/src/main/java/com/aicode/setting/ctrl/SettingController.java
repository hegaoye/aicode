/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.ctrl;

import com.aicode.core.entity.R;
import com.aicode.core.exceptions.BaseException;
import com.aicode.setting.entity.Setting;
import com.aicode.setting.service.SettingService;
import com.aicode.setting.vo.SettingSaveVO;
import com.aicode.setting.vo.SettingVO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 设置
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/setting")
@Slf4j
@Api(value = "设置控制器", tags = "设置控制器")
public class SettingController {
    @Autowired
    private SettingService settingService;


    /**
     * 创建 设置
     *
     * @return R
     */
    @ApiOperation(value = "创建Setting", notes = "创建Setting")
    @PostMapping("/build")
    public SettingSaveVO build(@ApiParam(name = "创建Setting", value = "传入json格式", required = true)
                               @RequestBody SettingSaveVO settingSaveVO) {
        if (null == settingSaveVO) {
            return null;
        }
        Setting newSetting = new Setting();
        BeanUtils.copyProperties(settingSaveVO, newSetting);

        settingService.save(newSetting);

        settingSaveVO = new SettingSaveVO();
        BeanUtils.copyProperties(newSetting, settingSaveVO);
        log.debug(JSON.toJSONString(settingSaveVO));
        return settingSaveVO;
    }


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
    public R load(String k) {
        Assert.hasText(k, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Setting setting = settingService.getOne(new LambdaQueryWrapper<Setting>()
                .eq(Setting::getK, k));
        return R.success(setting.getV());
    }

    /**
     * 查询设置信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询Setting信息集合", notes = "查询Setting信息集合")
    @GetMapping(value = "/list")
    public R list() {
        List<Setting> settingList = settingService.list();
        return R.success(settingList);
    }


    /**
     * 修改 设置
     *
     * @return R
     */
    @ApiOperation(value = "修改Setting", notes = "修改Setting")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "k", value = "键", required = true, paramType = "query"),
            @ApiImplicitParam(name = "v", value = "值", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "说明", paramType = "query")
    })
    @RequestMapping(value = "/modify", method = {RequestMethod.PUT, RequestMethod.POST})
    public R modify(@ApiIgnore SettingVO settingVO) {
        Setting newSetting = new Setting();
        BeanUtils.copyProperties(settingVO, newSetting);
        settingService.update(newSetting, new LambdaQueryWrapper<Setting>()
                .eq(Setting::getK, settingVO.getK()));
        return R.success();
    }


    /**
     * 删除 设置
     *
     * @return R
     */
    @ApiOperation(value = "删除Setting", notes = "删除Setting")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore SettingVO settingVO) {
        Setting newSetting = new Setting();
        BeanUtils.copyProperties(settingVO, newSetting);
        settingService.remove(new LambdaQueryWrapper<Setting>()
                .eq(Setting::getId, settingVO.getId()));
        return R.success("删除成功");
    }

}
