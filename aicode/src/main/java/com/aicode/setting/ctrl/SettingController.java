/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.ctrl;


import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.setting.entity.Setting;
import com.aicode.setting.service.SettingService;
import com.aicode.setting.vo.SettingSaveVO;
import com.aicode.setting.vo.SettingVO;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设置
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/setting")
@Slf4j
@Tag(name = "设置控制器", description = "设置控制器")
public class SettingController {
    @Autowired
    private SettingService settingService;

    @Operation(summary = "创建Setting", description = "创建Setting")
    @PostMapping("/build")
    public SettingSaveVO build(@RequestBody SettingSaveVO settingSaveVO) {
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

    @Operation(summary = "加载一个系统中的参数设置", description = "加载一个系统中的参数设置")
    @Parameters({
            @Parameter(name = "k", description = "键", required = true)
    })
    @GetMapping("/load")
    public R load(String k) {
        Assert.hasText(k, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Setting setting = settingService.getOne(new LambdaQueryWrapper<Setting>()
                .eq(Setting::getK, k));
        return R.success(setting.getV());
    }


    @Operation(summary = "查询Setting信息集合", description = "查询Setting信息集合")
    @GetMapping(value = "/list")
    public R list() {
        List<Setting> settingList = settingService.list();
        return R.success(settingList);
    }


    @Operation(summary = "修改Setting", description = "修改Setting")
    @Parameters({
            @Parameter(name = "k", description = "键", required = true),
            @Parameter(name = "v", description = "值", required = true),
            @Parameter(name = "description", description = "说明")
    })
    @RequestMapping(value = "/modify", method = {RequestMethod.PUT, RequestMethod.POST})
    public R modify(@Parameter(hidden = true) SettingVO settingVO) {
        Setting newSetting = new Setting();
        BeanUtils.copyProperties(settingVO, newSetting);
        settingService.update(newSetting, new LambdaQueryWrapper<Setting>()
                .eq(Setting::getK, settingVO.getK()));
        return R.success();
    }

    @Operation(summary = "删除Setting", description = "删除Setting")
    @Parameters({
            @Parameter(name = "id", description = "id")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) SettingVO settingVO) {
        Setting newSetting = new Setting();
        BeanUtils.copyProperties(settingVO, newSetting);
        settingService.remove(new LambdaQueryWrapper<Setting>()
                .eq(Setting::getId, settingVO.getId()));
        return R.success("删除成功");
    }

}
