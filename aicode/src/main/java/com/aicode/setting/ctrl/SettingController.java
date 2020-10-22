/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.ctrl;

import com.aicode.setting.entity.Setting;
import com.aicode.setting.service.SettingService;
import com.aicode.setting.vo.SettingPageVO;
import com.aicode.setting.vo.SettingSaveVO;
import com.aicode.setting.vo.SettingVO;
import com.aicode.core.entity.Page;
import com.aicode.core.entity.PageVO;
import com.aicode.core.entity.R;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
     * 查询设置信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询Setting信息集合", notes = "查询Setting信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<SettingVO> list(@ApiIgnore SettingPageVO settingVO, Integer curPage, Integer pageSize) {
        Page<Setting> page = new Page<>(pageSize, curPage);
        QueryWrapper<Setting> queryWrapper = new QueryWrapper<>();
        int total = settingService.count(queryWrapper);
        PageVO<SettingVO> settingVOPageVO = new PageVO<>();
        if (total > 0) {
            List<Setting> settingList = settingService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            settingVOPageVO.setTotalRow(total);
            settingVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(settingList),SettingVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return settingVOPageVO;
    }


    /**
     * 修改 设置
     *
     * @return R
     */
    @ApiOperation(value = "修改Setting", notes = "修改Setting")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改Setting", value = "传入json格式", required = true)
                          @RequestBody SettingVO settingVO) {
        Setting newSetting = new Setting();
        BeanUtils.copyProperties(settingVO, newSetting);
        boolean isUpdated = settingService.update(newSetting, new LambdaQueryWrapper<Setting>()
                .eq(Setting::getId, settingVO.getId()));
        return isUpdated;
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
