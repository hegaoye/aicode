/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */

package ${basePackage}.upload.ctrl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.JSchException;
import ${basePackage}.core.base.BaseUploadCtrl;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.entity.UploadBasicEntity;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.tools.*;
import ${basePackage}.core.tools.redis.RedisKey;
import ${basePackage}.course.service.CourseWareSV;
import ${basePackage}.goods.service.GoodsSV;
import ${basePackage}.tutor.service.AssistantSV;
import ${basePackage}.tutor.service.StudentSV;
import ${basePackage}.tutor.service.TutorSV;
import ${basePackage}.upload.service.UploadSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * 文件上传控制器
 */
@Controller
@RequestMapping("/upload")
@Api(value = "文件上传", description = "文件上传")
public class UploadCtrl extends BaseUploadCtrl {
    private final static Logger logger = LoggerFactory.getLogger(UploadCtrl.class);

    @Reference
    private GoodsSV goodsSV;

    @Reference
    private AssistantSV assistantSV;

    @Reference
    private StudentSV studentSV;

    @Reference
    private TutorSV tutorSV;

    @Reference
    private CourseWareSV courseWareSV;

    @Reference
    private UploadSV uploadSV;

    /**
     * 上传商品图片
     * 1.上传文件
     * 2.更新缓存
     *
     * @return BeanRet
     */
    @ApiOperation(value = "上传商品图片", notes = "上传商品图片，接口接收的是springmvc的文件mulpartfile类，请注意这个特性，并且是一个单文件模式")
    @PostMapping("/goods/goodsimg")
    @ResponseBody
    public BeanRet goodsImage(@ApiParam(value = "springmvc文件对象", required = true) @RequestParam MultipartFile file,
                              @ApiParam(value = "文件唯一标识编码", required = true) @RequestParam String uid) throws Exception {
        BeanRet beanRet = this.upload(uid, file.getInputStream(), "goods");
        if (!beanRet.getSuccess()) {
            return beanRet;
        }
        UploadBasicEntity uploadBasicEntity = uploadSV.upload(null, uid, JSON.parseArray(beanRet.getData().toString(), String.class));
        if (uploadBasicEntity.getUniqueCode() != null) {
            goodsSV.update4Images(uploadBasicEntity.getUniqueCode(), uploadBasicEntity.getPaths().get(0));
        }
        return beanRet;

    }

    /**
     * 上传课件文件
     * 1.上传文件
     * 2.更新缓存
     *
     * @return BeanRet
     */
    @ApiOperation(value = "上传课件文件", notes = "上传课件文件，接口接收的是springmvc的文件mulpartfile类，请注意这个特性，并且是一个单文件模式")
    @PostMapping("/courseware/file")
    @ResponseBody
    public BeanRet coursewareFile(@ApiParam(value = "springmvc文件对象", required = true) @RequestParam MultipartFile file,
                                  @ApiParam(value = "文件唯一标识编码", required = true) @RequestParam String uid) throws Exception {
        BeanRet beanRet = this.upload(uid, file, "/");
        if (!beanRet.getSuccess()) {
            return beanRet;
        }

        List<String> list = JSON.parseArray(beanRet.getData().toString(), String.class);
        String path = list.get(0).replaceFirst("http://", "");
        path = path.substring(path.indexOf("/") + 1);

        if (file.getOriginalFilename().toLowerCase().contains(".zip")) {
            this.unzip(path);
        }

        path = path.substring(path.lastIndexOf("/"), path.indexOf(".zip"));
        path = path.endsWith("/") ? path : path + "/";
        path = path + "index.html";
        list.clear();
        list.add(path);
        UploadBasicEntity uploadBasicEntity = uploadSV.upload(null, uid, list);
        int size = 0;
        if (file.getSize() > 1024) {
            size = (int) (file.getSize() / 1024);
        }
        if (uploadBasicEntity.getUniqueCode() != null) {
            courseWareSV.update4Images(uploadBasicEntity.getUniqueCode(), uploadBasicEntity.getPaths().get(0), size);
        }
        uploadBasicEntity.setData(size);
        uploadBasicEntity.setPaths(list);
        redisUtils.set(RedisKey.genUploadKey(uid), JSON.toJSONString(uploadBasicEntity));
        return beanRet;

    }


    /**
     * 上传老师头像
     * 1.上传文件
     * 2.更新缓存
     *
     * @return BeanRet
     */
    @ApiOperation(value = "上传老师头像", notes = "上传老师头像，接口接收的是springmvc的文件mulpartfile类，请注意这个特性，并且是一个单文件模式")
    @PostMapping("/tutor/avatar")
    @ResponseBody
    public BeanRet tutorAvatar(@ApiParam(value = "springmvc文件对象", required = true) String file,
                               @ApiParam(value = "文件唯一标识编码", required = true) @RequestParam String uid) throws Exception {
        Assert.hasText(file, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(uid, BaseException.BaseExceptionEnum.Empty_Param.toString());
        byte[] bytes = Base64Utils.decodeFromString(file);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        BeanRet beanRet = this.upload(uid, in, "tutor/avatar");
        UploadBasicEntity uploadBasicEntity = uploadSV.upload(null, uid, JSON.parseArray(beanRet.getData().toString(), String.class));
        if (uploadBasicEntity.getUniqueCode() != null) {
            tutorSV.update4Images(uploadBasicEntity.getUniqueCode(), uploadBasicEntity.getPaths().get(0));
        }

        return beanRet;
    }

    /**
     * 上传助教头像
     * 1.上传文件
     * 2.更新缓存
     *
     * @return BeanRet
     */
    @ApiOperation(value = "上传助教头像", notes = "上传助教头像，接口接收的是springmvc的文件mulpartfile类，请注意这个特性，并且是一个单文件模式")
    @PostMapping("/assistant/avatar")
    @ResponseBody
    public BeanRet assistantAvatar(@ApiParam(value = "上传文件对象", required = true) String file,
                                   @ApiParam(value = "文件唯一标识编码", required = true) @RequestParam String uid) throws Exception {
        Assert.hasText(file, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(uid, BaseException.BaseExceptionEnum.Empty_Param.toString());
        byte[] bytes = Base64Utils.decodeFromString(file);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        BeanRet beanRet = this.upload(uid, in, "assistant/avatar");
        UploadBasicEntity uploadBasicEntity = uploadSV.upload(null, uid, JSON.parseArray(beanRet.getData().toString(), String.class));
        if (uploadBasicEntity.getUniqueCode() != null) {
            assistantSV.update4Images(uploadBasicEntity.getUniqueCode(), uploadBasicEntity.getPaths().get(0));
        }

        return beanRet;

    }

    /**
     * 上传学生头像
     * 1.上传文件
     * 2.更新缓存
     *
     * @return BeanRet
     */
    @ApiOperation(value = "上传学生头像", notes = "上传学生头像")
    @PostMapping("/student/avatar")
    @ResponseBody
    public BeanRet studentAvatar(@ApiParam(value = "上传文件对象", required = true) String file,
                                 @ApiParam(value = "文件唯一标识编码", required = true) @RequestParam String uid) throws Exception {
        Assert.hasText(file, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(uid, BaseException.BaseExceptionEnum.Empty_Param.toString());
        byte[] bytes = Base64Utils.decodeFromString(file);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        BeanRet beanRet = this.upload(uid, in, "student/avatar");
        UploadBasicEntity uploadBasicEntity = uploadSV.upload(null, uid, JSON.parseArray(beanRet.getData().toString(), String.class));
        if (uploadBasicEntity.getUniqueCode() != null) {
            studentSV.update4Images(uploadBasicEntity.getUniqueCode(), uploadBasicEntity.getPaths().get(0));
        }

        return beanRet;

    }


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


    /**
     * 解压
     *
     * @param path 路径
     */
    private void unzip(String path) {
        Executors.cacheThreadExecutor(new Runnable() {
            @Override
            public void run() {
                try {
                    SSH ssh = new SSH("192.168.10.210", 22, "root", "0");
                    SSHResInfo resInfo = ssh.sendCmd("unzip -d /data/www/  /data/ftp/" + path);
                    logger.info(resInfo.toString());
                    ssh.close();
                } catch (JSchException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    @ApiOperation(value = "上传商品图片", notes = "上传商品图片，接口接收的是springmvc的文件mulpartfile类，请注意这个特性，并且是一个单文件模式")
//    @GetMapping("/test")
//    @ResponseBody
//    public BeanRet test() {
//        try {
//            //创建ftpClient对象
//            FTPClient ftpClient = new FTPClient();
//            //创建ftp链接，默认是21端口
//            ftpClient.connect("192.168.10.210", 21);
//
//            //登录ftp服务器，使用用户名和密码
//            ftpClient.login("ftpimg", "0");
//            System.out.printf(${basePackage}.core.tools.JSON.toJSONString(ftpClient.listFiles()));
//
//            //上传文件
//            //读取本地文件
//            FileInputStream inputStream = new FileInputStream(new File("/home/upload/basic/goods/20170911040341/1505102621483-1.jpg"));
//
//            //设置上传的路径
//            ftpClient.changeWorkingDirectory("/");
//
//            //修改上传格式
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            //第一个参数：服务器端文档名
//            //第二个参数，上传文档的inputStream
//            ftpClient.storeFile("1111.png", inputStream);
//            //关闭链接
//            ftpClient.logout();
//            return BeanRet.create(true, "");
//        } catch (Exception e) {
//            logger.error("<< " + e.getMessage());
//            e.printStackTrace();
//            return BeanRet.create(false, "upload failed");
//        }
//    }


}
