/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.display.ctrl;

import com.alibaba.fastjson.JSON;
import io.aicode.base.core.BeanRet;
import io.aicode.core.entity.Page;
import io.aicode.core.tools.StringTools;
import io.aicode.core.tools.redis.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import springfox.documentation.annotations.ApiIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import io.aicode.core.base.BaseCtrl;
import io.aicode.display.facade.DisplayAttributeSV;
import io.aicode.display.entity.DisplayAttribute;


/**
 * 显示属性
 *
 * @author lixin
 */
@Controller
@RequestMapping("/displayAttribute")
@Api(value = "显示属性控制器", description = "显示属性控制器")
public class DisplayAttributeCtrl extends BaseCtrl {
    private final static Logger logger = LoggerFactory.getLogger(DisplayAttributeCtrl.class);

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource
    protected RedisUtils redisUtils;

    @Resource
    private DisplayAttributeSV displayAttributeSV;


    /**
     * 查询显示属性详情信息
     *
     * @param mapFieldColumnCode 字段编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询显示属性详情信息", notes = "查询显示属性详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapFieldColumnCode", value = "字段编码", paramType = "query")
    })
    @GetMapping(value = "/loadByMapFieldColumnCode")
    @ResponseBody
    public BeanRet loadByMapFieldColumnCode(@PathVariable String mapFieldColumnCode) {
        if (StringTools.isEmpty(mapFieldColumnCode)) {
            return BeanRet.create(false, "");
        }

        DisplayAttribute displayAttribute = displayAttributeSV.loadByMapFieldColumnCode(mapFieldColumnCode);
        logger.info(JSON.toJSONString(displayAttribute));
        return BeanRet.create(true, "查询详情信息", displayAttribute);
    }


    /**
     * 查询显示属性信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询显示属性信息集合", notes = "查询显示属性信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "mapFieldColumnCode", value = "字段编码", paramType = "query"),
            @ApiImplicitParam(name = "isRequired", value = "是否必填 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isInsert", value = "是否插入", paramType = "query"),
            @ApiImplicitParam(name = "isDeleteCondition", value = "是否是删除条件", paramType = "query"),
            @ApiImplicitParam(name = "isAllowUpdate", value = "是否允许修改 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isListPageDisplay", value = "是否分页列表显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isDetailPageDisplay", value = "是否详情页显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isQueryRequired", value = "是否是查询条件 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isLineNew", value = "是否换行", paramType = "query"),
            @ApiImplicitParam(name = "matchType", value = "匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in", paramType = "query"),
            @ApiImplicitParam(name = "displayType", value = "显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar", paramType = "query"),
            @ApiImplicitParam(name = "displayName", value = "显示列名称", paramType = "query"),
            @ApiImplicitParam(name = "displayNo", value = "显示顺序", paramType = "query"),
            @ApiImplicitParam(name = "fieldValidationMode", value = "字段验证方式", paramType = "query"),
            @ApiImplicitParam(name = "validateText", value = "验证提示语", paramType = "query"),
            @ApiImplicitParam(name = "displayCss", value = "显示css样式", paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore DisplayAttribute displayAttribute, @ApiIgnore Page<DisplayAttribute> page) {
        if (page == null) {
            return BeanRet.create(false, "分页对象不能为空");
        }
        page.setParams(JSON.parseObject(JSON.toJSONString(displayAttribute)));
        page = displayAttributeSV.getList(page);
        return BeanRet.create(true, "", page);
    }

    /**
     * 创建显示属性
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建显示属性", notes = "创建显示属性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapFieldColumnCode", value = "字段编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "isRequired", value = "是否必填 Y,N", paramType = "query", required = true),
            @ApiImplicitParam(name = "isInsert", value = "是否插入", paramType = "query", required = true),
            @ApiImplicitParam(name = "isDeleteCondition", value = "是否是删除条件", paramType = "query", required = true),
            @ApiImplicitParam(name = "isAllowUpdate", value = "是否允许修改 Y,N", paramType = "query", required = true),
            @ApiImplicitParam(name = "isListPageDisplay", value = "是否分页列表显示 Y,N", paramType = "query", required = true),
            @ApiImplicitParam(name = "isDetailPageDisplay", value = "是否详情页显示 Y,N", paramType = "query", required = true),
            @ApiImplicitParam(name = "isQueryRequired", value = "是否是查询条件 Y,N", paramType = "query", required = true),
            @ApiImplicitParam(name = "isLineNew", value = "是否换行", paramType = "query", required = true),
            @ApiImplicitParam(name = "matchType", value = "匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in", paramType = "query", required = true),
            @ApiImplicitParam(name = "displayType", value = "显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar", paramType = "query", required = true),
            @ApiImplicitParam(name = "displayName", value = "显示列名称", paramType = "query", required = true),
            @ApiImplicitParam(name = "displayNo", value = "显示顺序", paramType = "query", required = true),
            @ApiImplicitParam(name = "fieldValidationMode", value = "字段验证方式", paramType = "query", required = true),
            @ApiImplicitParam(name = "validateText", value = "验证提示语", paramType = "query", required = true),
            @ApiImplicitParam(name = "displayCss", value = "显示css样式", paramType = "query", required = true)
    })
    @PostMapping("/save")
    @ResponseBody
    public BeanRet save(@ApiIgnore DisplayAttribute displayAttribute) {
        displayAttributeSV.save(displayAttribute);
        return BeanRet.create(true, "保存成功");
    }


    /**
     * 修改显示属性
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改显示属性", notes = "修改显示属性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapFieldColumnCode", value = "字段编码", paramType = "query"),
            @ApiImplicitParam(name = "isRequired", value = "是否必填 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isInsert", value = "是否插入", paramType = "query"),
            @ApiImplicitParam(name = "isDeleteCondition", value = "是否是删除条件", paramType = "query"),
            @ApiImplicitParam(name = "isAllowUpdate", value = "是否允许修改 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isListPageDisplay", value = "是否分页列表显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isDetailPageDisplay", value = "是否详情页显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isQueryRequired", value = "是否是查询条件 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isLineNew", value = "是否换行", paramType = "query"),
            @ApiImplicitParam(name = "matchType", value = "匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in", paramType = "query"),
            @ApiImplicitParam(name = "displayType", value = "显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar", paramType = "query"),
            @ApiImplicitParam(name = "displayName", value = "显示列名称", paramType = "query"),
            @ApiImplicitParam(name = "displayNo", value = "显示顺序", paramType = "query"),
            @ApiImplicitParam(name = "fieldValidationMode", value = "字段验证方式", paramType = "query"),
            @ApiImplicitParam(name = "validateText", value = "验证提示语", paramType = "query"),
            @ApiImplicitParam(name = "displayCss", value = "显示css样式", paramType = "query")
    })
    @PutMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore DisplayAttribute displayAttribute) {
        displayAttributeSV.update(displayAttribute);
        return BeanRet.create(true, "修改成功");
    }

    /**
     * 删除显示属性
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除显示属性", notes = "删除显示属性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapFieldColumnCode", value = "字段编码", paramType = "query", required = true)
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(Long id, String mapFieldColumnCode) {
        if (id == null) {
            return BeanRet.create(false, "不能为空");
        }
        if (mapFieldColumnCode == null) {
            return BeanRet.create(false, "字段编码不能为空");
        }
        displayAttributeSV.delete(id, mapFieldColumnCode);
        return BeanRet.create(true, "删除DisplayAttribute成功");
    }

}
