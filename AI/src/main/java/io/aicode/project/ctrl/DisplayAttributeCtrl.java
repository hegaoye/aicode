/*
 *  *
 *                       http://www.aicode.io
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */
package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import io.aicode.core.base.BaseCtrl;
import io.aicode.core.entity.BeanRet;
import io.aicode.core.entity.Page;
import io.aicode.core.tools.StringTools;
import io.aicode.core.tools.redis.RedisUtils;
import io.aicode.project.entity.DisplayAttribute;
import io.aicode.project.entity.MapClassTable;
import io.aicode.project.entity.MapFieldColumn;
import io.aicode.project.service.DisplayAttributeSV;
import io.aicode.project.service.MapClassTableSV;
import io.aicode.project.service.MapFieldColumnSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;


/**
 * 显示属性
 *
 * @author berton
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

    @Resource
    private MapClassTableSV mapClassTableSV;

    @Resource
    private MapFieldColumnSV mapFieldColumnSV;


    /**
     * 查询显示属性详情信息
     *
     * @param id
     * @return BeanRet
     */
    @ApiOperation(value = "查询显示属性详情信息", notes = "查询显示属性详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query")
    })
    @GetMapping(value = "/loadById")
    @ResponseBody
    public BeanRet loadById(@PathVariable Long id) {
        if (id == null) {
            return BeanRet.create();
        }

        DisplayAttribute displayAttribute = displayAttributeSV.load(id);
        logger.info(JSON.toJSONString(displayAttribute));
        return BeanRet.create(true, "查询详情信息", displayAttribute);
    }


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
            return BeanRet.create();
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
            @ApiImplicitParam(name = "isAllowUpdate", value = "是否允许修改 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isListPageDisplay", value = "是否分页列表显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isDetailPageDisplay", value = "是否详情页显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isQueryRequired", value = "是否是查询条件 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "displayType", value = "显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar", paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore DisplayAttribute displayAttribute, @ApiIgnore Page<DisplayAttribute> page) {
        if (page == null) {
            return BeanRet.create(false, "分页对象不能为空");
        }
        page.setParams(JSON.parseObject(JSON.toJSONString(displayAttribute)));
        page = displayAttributeSV.getList(page);
        return BeanRet.create(true, "查询成功", page);
    }

    /**
     * 查询项目所有的表信息
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询项目所有的表信息", notes = "查询项目所有的表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query", required = true),
    })
    @GetMapping(value = "/listClassTable")
    @ResponseBody
    public BeanRet listClassTable(String projectCode) {
        List<MapClassTable> mapClassTables = mapClassTableSV.query(projectCode);
        return BeanRet.create(true, "查询成功", mapClassTables);
    }

    /**
     * 查询项目所有的表信息
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询项目所有的表信息", notes = "查询项目所有的表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "映射编码", paramType = "query", required = true),
    })
    @GetMapping(value = "/listFields")
    @ResponseBody
    public BeanRet listFields(String mapClassTableCode) {
        List<MapFieldColumn> mapFieldColumns = mapFieldColumnSV.listFields(mapClassTableCode);
        return BeanRet.create(true, "查询成功", mapFieldColumns);
    }

    /**
     * 创建显示属性
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建显示属性", notes = "创建显示属性")
    @PostMapping("/save")
    @ResponseBody
    public BeanRet save(@RequestBody List<DisplayAttribute> displayAttributes) {
        if(displayAttributes != null && displayAttributes.size() >0) {
            for (DisplayAttribute displayAttribute: displayAttributes) {
                displayAttributeSV.save(displayAttribute);
            }
            return BeanRet.create(true, "保存成功");
        } else {
            return BeanRet.create(true, "参数为空");
        }
    }


    /**
     * 修改显示属性
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改显示属性", notes = "修改显示属性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "mapFieldColumnCode", value = "字段编码", paramType = "query"),
            @ApiImplicitParam(name = "isRequired", value = "是否必填 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isAllowUpdate", value = "是否允许修改 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isListPageDisplay", value = "是否分页列表显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isDetailPageDisplay", value = "是否详情页显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isQueryRequired", value = "是否是查询条件 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "displayType", value = "显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar", paramType = "query")
    })
    @PutMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore DisplayAttribute displayAttribute) {
        displayAttributeSV.update(displayAttribute);
        return BeanRet.create(true, "修改成功");
    }

}
