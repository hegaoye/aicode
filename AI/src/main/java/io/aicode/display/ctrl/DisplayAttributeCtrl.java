/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.display.ctrl;

import com.alibaba.fastjson.JSON;
import io.aicode.base.BaseCtrl;
import io.aicode.base.core.BeanRet;
import io.aicode.base.tools.StringTools;
import io.aicode.display.entity.DisplayAttribute;
import io.aicode.display.facade.DisplayAttributeSV;
import io.aicode.project.entity.MapFieldColumn;
import io.aicode.project.service.MapFieldColumnSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


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
    private DisplayAttributeSV displayAttributeSV;

    @Resource
    private MapFieldColumnSV mapFieldColumnSV;


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
     * 查询字段信息
     *
     * @return 分页对象
     */
    /*@ApiOperation(value = "查询字段信息", notes = "查询字段信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "映射编码", paramType = "query"),
    })
    @GetMapping(value = "/fieldsList")
    @ResponseBody
    public BeanRet fieldsList(String mapClassTableCode) {
        if (StringTools.isEmpty(mapClassTableCode)) {
            return BeanRet.create(false, "分页对象不能为空");
        }
        Map<String, Object> params = new HashedMap();
        params.put("mapClassTableCode", mapClassTableCode);
        List<MapFieldColumn> mapFieldColumns = mapFieldColumnSV.queryList(params);
        return BeanRet.create(true, "查询成功", mapFieldColumns);
    }*/

    /**
     * 查询字段信息
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询字段信息", notes = "查询字段信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "映射编码", paramType = "query"),
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(String mapClassTableCode) {
        if (StringTools.isEmpty(mapClassTableCode)) {
            return BeanRet.create(false, "分页对象不能为空");
        }
        Map<String, Object> params = new HashedMap();
        params.put("mapClassTableCode", mapClassTableCode);
        List<MapFieldColumn> mapFieldColumns = mapFieldColumnSV.queryList(params);
        String fieldCode;
        DisplayAttribute displayAttribute;
        if (!mapFieldColumns.isEmpty()) {
            for (MapFieldColumn mapFieldColumn : mapFieldColumns) {
                fieldCode = mapFieldColumn.getCode();
                displayAttribute = displayAttributeSV.loadByMapFieldColumnCode(fieldCode);
                mapFieldColumn.setDisplayAttribute(displayAttribute);
            }
        }
        return BeanRet.create(true, "查询成功", mapFieldColumns);
    }

    /**
     * 创建显示属性
     * 1.数据合法性验证
     * 2.遍历参数
     * ****2.1 字段编码是否存在
     * ****2.2 显示属性是否已经持久化，已持久化过进行更新，未持久化的持久化到数据库
     * 3.返回结果
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建显示属性", notes = "创建显示属性")
    @PostMapping("/save")
    @ResponseBody
    public BeanRet save(@RequestBody List<DisplayAttribute> displayAttributes) {
        if (displayAttributes == null || displayAttributes.size() <= 0) {
            return BeanRet.create(false, "参数不能为空");
        }
        displayAttributeSV.saveOrUpdate(displayAttributes);
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
