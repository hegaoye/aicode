package ${basePackage}.upload.ctrl;

import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.entity.ProgressVO;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.tools.redis.RedisKey;
import ${basePackage}.core.tools.redis.RedisUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 上传进度条控制器
 * Created by shangze on 2017/6/23.
 */
@Controller
@RequestMapping("/upload/progress")
@Api(value = "上传进度条控制器", description = "文件上传过程中上传进度的控制")
public class UploadProgressCtrl {
    @Resource
    private RedisUtils redisUtils;

    @ApiOperation(value = "查询上传进度", notes = "用于上传过程中，查看上传信息的完成进度")
    @GetMapping("/getProgress")
    @ResponseBody
    public BeanRet getProgress(@ApiParam(value = "文件唯一标识编码", required = true) @RequestParam String uid) {
        Assert.hasText(uid, BaseException.BaseExceptionEnum.Empty_Param.toString());
        ProgressVO progressVO = JSON.parseObject((String) redisUtils.get(RedisKey.genUploadProgressKey(uid)), ProgressVO.class);
        if (progressVO == null) {
            return BeanRet.create(false, "上传进度不存在");
        }
        return BeanRet.create(true, "成功", progressVO);
    }
}
