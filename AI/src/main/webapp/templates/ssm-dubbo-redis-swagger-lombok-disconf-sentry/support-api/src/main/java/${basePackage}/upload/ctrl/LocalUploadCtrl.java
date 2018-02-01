package ${basePackage}.upload.ctrl;

import com.alibaba.fastjson.JSON;
import ${basePackage}.core.base.BaseUploadCtrl;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.entity.UploadBasicEntity;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.tools.FileUpTools;
import ${basePackage}.core.tools.PathTools;
import ${basePackage}.core.tools.UuidTools;
import ${basePackage}.core.tools.redis.RedisKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 上传文件到本地控制器
 * Created by lixin on 2017/6/16.
 */
@Controller
@RequestMapping("/upload/local")
@Api(value = "上传文件到本地控制器", description = "上传文件到本地控制器")
public class LocalUploadCtrl extends BaseUploadCtrl {
    private final static Logger logger = LoggerFactory.getLogger(LocalUploadCtrl.class);

    /**
     * 上传文件到临时目录
     *
     * @return BeanRet
     */
    @ApiOperation(value = "上传文件到临时目录", notes = "上传文件到临时目录,通过内存中的uidKey来获取文件路径项目中获取，对外不暴露系统内部文件存储路径" +
            "代码案例：String uploadBasicEntityJSON = (String) redisUtils.get(RedisKey.genUploadKey(uid));" +
            "UploadBasicEntity uploadBasicEntity = JSON.parseObject(uploadBasicEntityJSON, UploadBasicEntity.class);")
    @PostMapping("/file")
    @ResponseBody
    public BeanRet uploadLocal(@ApiParam(value = "springmvc文件对象", required = true) @RequestParam MultipartFile file) throws IOException {
        Assert.notNull(file, BaseException.BaseExceptionEnum.Empty_Param.toString());
        String fileUid = UuidTools.getUUIDString();
        String name = file.getOriginalFilename();
        name = name.substring(name.lastIndexOf("."));
        BeanRet beanRet = FileUpTools.getInstance().uploadFileToLocal(file.getInputStream(), PathTools.getLocalRelative(), fileUid + name);
        if (beanRet.getSuccess()) {
            UploadBasicEntity uploadBasicEntity = new UploadBasicEntity(fileUid, null, beanRet.getData());
            logger.info("上传路径:" + beanRet.getData());
            redisUtils.set(RedisKey.genUploadLocalKey(fileUid), JSON.toJSONString(uploadBasicEntity));
            beanRet.setData(fileUid);
            return beanRet;
        }

        return BeanRet.create("");
    }


}
