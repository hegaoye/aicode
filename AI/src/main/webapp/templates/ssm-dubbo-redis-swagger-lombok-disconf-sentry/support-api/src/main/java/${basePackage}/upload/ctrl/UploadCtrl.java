package ${basePackage}.upload.ctrl;

import com.alibaba.fastjson.JSON;
import ${basePackage}.core.base.BaseUploadCtrl;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.entity.UploadBasicEntity;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.tools.DateTools;
import ${basePackage}.core.tools.FileUpTools;
import ${basePackage}.core.tools.PathTools;
import ${basePackage}.core.tools.redis.RedisKey;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;

/**
 * 文件上传控制器
 */
@Controller
@RequestMapping("/upload")
@Api(value = "文件上传", description = "文件上传")
public class UploadCtrl extends BaseUploadCtrl {
    private final static Logger logger = LoggerFactory.getLogger(UploadCtrl.class);

    /**
     * 上传文件
     *
     * @param uid     文件唯一标识编码
     * @param file    springmvc文件对象
     * @param pathTag 路径标识，比如：goods , tutor
     * @return BeanRet
     * @throws Exception
     */
    private BeanRet upload(String uid, MultipartFile file, String pathTag) throws Exception {
        Assert.hasText(uid, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.notNull(file, BaseException.BaseExceptionEnum.Empty_Param.toString());
        //获取缓存
        String uploadBasicEntityJSON = (String) redisUtils.get(RedisKey.genUploadKey(uid));
        UploadBasicEntity uploadBasicEntity = JSON.parseObject(uploadBasicEntityJSON, UploadBasicEntity.class);
        Assert.notNull(uploadBasicEntity, BaseException.BaseExceptionEnum.Empty_Param.toString());

        //1.上传文件
        String toSaveFileNewName = file.getOriginalFilename();
        pathTag = pathTag.endsWith("/") ? pathTag : pathTag + "/";
        String toSaveFilePath = PathTools.getCourseWareRelative(pathTag + DateTools.dateToNum14(new Date()));
        BeanRet beanRet = FileUpTools.getInstance().uploadFileToFtp(file.getInputStream(), toSaveFilePath, toSaveFileNewName);

        return beanRet;
    }


    private BeanRet upload(String uid, InputStream inputStream, String pathTag) throws Exception {
        Assert.hasText(uid, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.notNull(inputStream, BaseException.BaseExceptionEnum.Empty_Param.toString());
        //获取缓存
        String uploadBasicEntityJSON = (String) redisUtils.get(RedisKey.genUploadKey(uid));
        UploadBasicEntity uploadBasicEntity = JSON.parseObject(uploadBasicEntityJSON, UploadBasicEntity.class);
        Assert.notNull(uploadBasicEntity, BaseException.BaseExceptionEnum.Empty_Param.toString());

        //1.上传文件
        String toSaveFileNewName = String.valueOf(new Date().getTime());
        pathTag = pathTag.endsWith("/") ? pathTag : pathTag + "/";
        String toSaveFilePath = PathTools.getBasicRelative(pathTag + DateTools.stringToNum8(new Date()));
        BeanRet beanRet = FileUpTools.getInstance().uploadFileToFtp(inputStream, toSaveFilePath, toSaveFileNewName, FileUpTools.imgcp_default);
        return beanRet;
    }


}
