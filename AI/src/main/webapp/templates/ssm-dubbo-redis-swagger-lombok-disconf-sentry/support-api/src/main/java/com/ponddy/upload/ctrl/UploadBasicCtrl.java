/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于龐帝業務系统.目.
 */

package ${basePackage}.upload.ctrl;

import com.alibaba.fastjson.JSON;
import ${basePackage}.core.base.BaseUploadCtrl;
import ${basePackage}.core.common.CookieKey;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.entity.UploadBasicEntity;
import ${basePackage}.core.tools.CookieTools;
import ${basePackage}.core.tools.UuidTools;
import ${basePackage}.core.tools.redis.RedisKey;
import ${basePackage}.core.tools.redis.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lixin on 2017/6/3.
 */
@Controller
@RequestMapping("/upload/basic")
@Api(value = "文件上传基础支撑", description = "文件上传过程中的通用问题解决接口")
public class UploadBasicCtrl extends BaseUploadCtrl {
    private final static Logger logger = LoggerFactory.getLogger(UploadBasicCtrl.class);
    @Resource
    public RedisTemplate<String, Object> redisTemplate;
    @Resource
    RedisUtils redisUtils;

    /**
     * 生成文件uid
     * 1.生成uid
     * 2.写入缓存
     * 3.写入cookie
     *
     * @return
     */
    @ApiOperation(value = "获得文件uid号", notes = "上传业务中会有一种情况，创建对象和上传文件同时进行的问题，uid作为临时对象代理的出现时解决文件上传和业务接口分离的方案")
    @GetMapping("/uid")
    @ResponseBody
    public BeanRet genFileUid(HttpServletResponse response) {
        // 1.生成uid
        String fileUid = UuidTools.getUUIDString();
        // 2.写入缓存
        UploadBasicEntity uploadBasicEntity = new UploadBasicEntity(fileUid, null, null);
        logger.info("====> " + RedisKey.genUploadKey(fileUid));
        redisUtils.set(RedisKey.genUploadKey(fileUid), JSON.toJSONString(uploadBasicEntity));
        uploadBasicEntity = JSON.parseObject(redisUtils.get(RedisKey.genUploadKey(fileUid)).toString(), UploadBasicEntity.class);
        logger.info("====> " + ${basePackage}.core.tools.JSON.toJSONString(uploadBasicEntity));
        //3.写入cookie
        CookieTools.INSTANCE.setCode(CookieKey.UploadUidKey, fileUid, response);
        return BeanRet.create(true, "uid生成成功", fileUid);
    }

}
