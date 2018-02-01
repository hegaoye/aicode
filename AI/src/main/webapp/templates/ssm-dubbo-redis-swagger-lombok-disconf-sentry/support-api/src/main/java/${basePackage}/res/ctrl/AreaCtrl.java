package ${basePackage}.res.ctrl;

import com.alibaba.dubbo.config.annotation.Reference;
import ${basePackage}.area.entity.BasicArea;
import ${basePackage}.area.service.BasicAreaSV;
import ${basePackage}.core.base.BaseCtrl;
import ${basePackage}.core.entity.BeanRet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 区域管理
 * Created by lixin on 2017/6/9.
 */
@Controller
@RequestMapping("/res/area")
@Api(value = "区域管理", description = "系统中区域管理")
public class AreaCtrl extends BaseCtrl {
    private final static Logger logger = LoggerFactory.getLogger(AreaCtrl.class);

    @Reference
    private BasicAreaSV basicAreaSV;


    /**
     * 区域管理
     */
    @ApiOperation(value = "区域管理", notes = "区域管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "areacode", value = "省区域编码", required = true, paramType = "path")
    })
    @GetMapping("/{areacode}")
    @ResponseBody
    public BeanRet loadAreacode(@PathVariable String areacode) {
        BasicArea basicArea = basicAreaSV.load(areacode);
        return BeanRet.create(true, "", basicArea);
    }

    /**
     * 获得所有省
     */
    @ApiOperation(value = "区域管理", notes = "区域管理")
    @GetMapping("/provoices")
    @ResponseBody
    public BeanRet listProvoiceAll() {
        List<BasicArea> basicAreas = basicAreaSV.listProvoice();
        return BeanRet.create(true, "", basicAreas);
    }

    /**
     * 手动访问加载区域编号至redis缓存中
     *
     * @return
     */
    @ApiOperation(value = "加载区域编号至redis缓存中", notes = "加载区域编号至redis缓存中")
    @GetMapping("/queryAreas2Code")
    @ResponseBody
    public BeanRet queryAreas2Code() {
        boolean result = basicAreaSV.queryAll2Redis();
        return BeanRet.create(result, null);
    }
}
