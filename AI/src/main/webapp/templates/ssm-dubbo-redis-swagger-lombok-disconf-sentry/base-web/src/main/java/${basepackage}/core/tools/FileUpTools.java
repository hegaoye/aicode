/*
 * Copyright (c) 2016. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package ${basePackage}.core.tools;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.tools.ftp.FtpTool;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 文件上传工具类
 * 用于文件的上传，包括非图片，图片及其其他文件格式，并自动上到制定ftp服务器上
 */
public class FileUpTools {
    private final static Logger log = LoggerFactory.getLogger(FileUpTools.class);

    public static FtpTool ftp = null;
    private static final String PROJECTPATH = new HandleFuncs().getCurrentClassPath();//项目路径
    //图片压缩类型
    public static String imgcp_default = ConfigUtil.getValue("upload_img_compress_default", "upload_config.properties");
    //临时目录
    private static String upload_temp_dir = ConfigUtil.getValue("upload_temp_dir", "upload_config.properties");
    private static String IMG_COMPRESSTYPE_SPLIT = "!";//不同规格的图片压缩分割符号
    private static FileUpTools instance;


    public static FileUpTools getInstance() {
        if (instance == null) {
            instance = new FileUpTools();
        }
        return instance;
    }

    public FileUpTools() {
        if (StringUtils.isEmpty(upload_temp_dir)) upload_temp_dir = ConfigUtil.getValue("upload_temp_dir", "upload_config.properties");
        upload_temp_dir = upload_temp_dir.startsWith("/") ? upload_temp_dir : "/" + upload_temp_dir;
        upload_temp_dir = upload_temp_dir.endsWith("/") ? upload_temp_dir : upload_temp_dir + "/";
    }


    /**
     * 上传文件到本地临时目录下
     *
     * @param inputStream       文件流
     * @param toSaveFilePath    保存路径
     * @param toSaveFileNewName 保存名称
     * @return BeanRet
     */
    public BeanRet uploadFileToLocal(InputStream inputStream, String toSaveFilePath, String toSaveFileNewName) {
        toSaveFilePath = toSaveFilePath.startsWith("/") ? toSaveFilePath.substring(1) : toSaveFilePath;
        toSaveFilePath = toSaveFilePath.endsWith("/") ? toSaveFilePath : toSaveFilePath + "/";
        try {
//            if (!this.checkFileSize((long) inputStream.available())) return null;
            String filePath = PROJECTPATH + upload_temp_dir + toSaveFilePath + toSaveFileNewName;
            log.info(" >> " + filePath);
            File toUploadFile = new File(filePath);
            FileUtils.copyInputStreamToFile(inputStream, toUploadFile);/* 上传文件到临时目录下 */
            return BeanRet.create(true, "上传成功", filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解压zip
     * 1.解压文件
     * 2.扫描文件路径
     * 3.返回文件路径
     *
     * @param zipFile zip文件路径
     * @return 文件路径
     */
    public java.util.List<String> unzip(String zipFile) {
        //1.解压文件
        String dest = zipFile.substring(0, zipFile.lastIndexOf("/"));
        String destRelative = dest.substring(dest.lastIndexOf("/")).replace("/", "");
        ZipTools.unzip(zipFile, dest);
        //2.扫描文件路径
        log.info(zipFile.replace(".zip", ""));
        Collection<File> dirs = FileUtils.listFilesAndDirs(new File(zipFile.replace(".zip", "")), FileFilterUtils.and(EmptyFileFilter.NOT_EMPTY), DirectoryFileFilter.INSTANCE);
        List<String> filePaths = new ArrayList<>();
        for (File dir : dirs) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String path = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(destRelative)).replace(destRelative, "");
                        path = path.replace("\\", "/");
                        path = dest + path;
                        filePaths.add(path);
                    }
                }
            }
        }
        log.info(JSON.toJSONString(filePaths));
        //3.返回文件路径
        return filePaths;
    }


    /**
     * 上传到ftp服务器保存文件(非图使用)
     * 机制：文件名作为临时名称和ftp使用名称不再单独建立文件名
     *
     * @param inputStream       文件对象流数组
     * @param toSaveFileNewName 期望保存的文件名 [xxxx|xxxx.xxx]
     * @param toSaveFilePath    ftp保存路径 [/xxx/xxx/]
     * @return
     * @throws Exception
     */
    public BeanRet uploadFileToFtp(InputStream inputStream, String toSaveFilePath, String toSaveFileNewName) throws Exception {
        return this.uploadFileToFtp(new InputStream[]{inputStream}, toSaveFilePath, toSaveFileNewName, null);
    }

    /**
     * 上传到ftp服务器保存文件
     * 1.上传原图到项目临时目录下
     * 2.压缩不同规格图片上传到临时目录下
     * 3.原图+压缩图上传到ftp服务器目录下
     * 4.删除项目下临时文件
     * <p>
     * 机制：文件名作为临时名称和ftp使用名称不再单独建立文件名
     *
     * @param inputStream       文件对象流数组
     * @param toSaveFileNewName 期望保存的文件名 [xxxx|xxxx.xxx]
     * @param toSaveFilePath    ftp保存路径 [/xxx/xxx/]
     * @return
     * @throws Exception
     */
    public BeanRet uploadFileToFtp(InputStream inputStream, String toSaveFilePath, String toSaveFileNewName, String compressType) throws Exception {
        return this.uploadFileToFtp(new InputStream[]{inputStream}, toSaveFilePath, toSaveFileNewName, compressType);
    }


    /**
     * 上传到ftp服务器保存文件
     * 1.上传原文件到项目临时目录下
     * 2.压缩不同规格图片上传到临时目录下
     * 3.上传到ftp服务器目录下
     * 4.删除项目下临时文件
     * <p>
     * 机制：文件名作为临时名称和ftp使用名称不再单独建立文件名
     *
     * @param inputStreams      文件对象流数组
     * @param toSaveFileNewName 期望保存的文件名 [xxxx|xxxx.xxx]
     * @param toSaveFilePath    ftp保存路径 [/xxx/xxx/]
     * @return
     * @throws Exception
     */
    public BeanRet uploadFileToFtp(InputStream[] inputStreams, String toSaveFilePath, String toSaveFileNewName, String compressType) throws Exception {
        //1.上传原文件到项目临时目录下
        java.util.List<String> fileRelativePaths = null;
        if (compressType != null) {
            fileRelativePaths = this.uploadFileToLocal(inputStreams, toSaveFilePath, toSaveFileNewName);
        } else {
            fileRelativePaths = new ArrayList<>();
            for (InputStream inputStream : inputStreams) {
                String path = this.uploadFileToLocal(inputStream, toSaveFilePath, toSaveFileNewName).getData().toString();
                fileRelativePaths.add(path);
            }

            //处理zip的文件
//            if (toSaveFileNewName.toLowerCase().contains(".zip")) {

//                List<String> unzipPaths = null;
//                for (String path : fileRelativePaths) {
//                    unzipPaths = this.unzip(path);
//                }
//                if (unzipPaths != null && !unzipPaths.isEmpty()) fileRelativePaths.addAll(unzipPaths);
//            }
        }

        log.info("上传原图到项目临时目录下 >> " + JSON.toJSONString(fileRelativePaths));
        if (fileRelativePaths == null || fileRelativePaths.size() == 0) {
            return BeanRet.create(false, "上传失败！");
        }
        if (compressType != null) {
            //2.压缩不同规格图片上传到临时目录下
            java.util.List<String> pressFileRelativePaths = this.pressImg(fileRelativePaths, compressType == null ? FileUpTools.imgcp_default : compressType);
            log.info("压缩不同规格图片上传到临时目录下 >> " + JSON.toJSONString(pressFileRelativePaths));
            if (fileRelativePaths != null && pressFileRelativePaths != null) {
                fileRelativePaths.addAll(pressFileRelativePaths);
            }
        }
        //3.上传到ftp服务器目录下
        java.util.List<String> fileAbsolutePaths = this.uploadFileToFtp(fileRelativePaths.toArray(new String[fileRelativePaths.size()]));
        log.info("上传到ftp绝对路径为：" + JSON.toJSONString(fileAbsolutePaths));
//        if (toSaveFileNewName.toLowerCase().contains(".zip")) {
//            SSH ssh = new SSH("192.168.10.210", 22, "root", "0");
//            String path = fileAbsolutePaths.get(0).replaceFirst("http://", "");
//            path = path.substring(path.indexOf("/") + 1);
//            SSHResInfo resInfo = ssh.sendCmd("unzip -d /data/www/  /data/ftp/" + path);
//            log.info(resInfo.toString());
//            ssh.close();
//        }
//        java.util.concurrent.Executors.newSingleThreadExecutor().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //4.删除项目下临时文件
//                    String toSaveFilePath1 = toSaveFilePath.substring(toSaveFilePath.indexOf("/") + 1);
//                    toSaveFilePath1 = toSaveFilePath1.substring(0, toSaveFilePath1.indexOf("/"));
//                    File file = new File(PROJECTPATH + upload_temp_dir + toSaveFilePath1);
//                    FileUtils.deleteDirectory(file);
//                    log.debug("删除临时文件：" + PROJECTPATH + fileAbsolutePath);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
        if (!fileAbsolutePaths.isEmpty()) {
            return BeanRet.create(true, "上传成功！", JSON.toJSONString(fileAbsolutePaths));
        } else {
            return BeanRet.create(false, "上传失败！");
        }
    }

    /**
     * 压缩图片
     *
     * @param sourceFilePaths 源文件路径集合
     * @param compressTpye    压缩类型
     * @return 压缩后路径集合
     */
    private java.util.List<String> pressImg(java.util.List<String> sourceFilePaths, String compressTpye) throws IOException {
        java.util.List<String> pressImgs = new ArrayList<>();
        for (String sourceFilePath : sourceFilePaths) {
            for (String compressType : compressTpye.split(",")) {
                //复制压缩文件 xxx.jpg!200x200.jpg  xxxx.jpg!400x400.jpg
                String pressImgFilePath = sourceFilePath + IMG_COMPRESSTYPE_SPLIT + compressType + "." + this.getfsuffix(sourceFilePath);
                File sourceFile = new File(sourceFilePath);
                File pressFile = new File(pressImgFilePath);
                log.info("复制压缩文件 >> " + pressImgFilePath);
                FileUtils.copyFile(sourceFile, pressFile);

                //获取宽高的压缩像素
                String[] compressTypeArr = compressType.split("x");
                int limit_width = Integer.parseInt(compressTypeArr[0]);
                if (limit_width == 0) {
                    limit_width = 100000;//意为不限制宽
                }
                int limit_height = Integer.parseInt(compressTypeArr[1]);
                if (limit_height == 0) {
                    limit_height = 100000;//意为不限制高
                }
                //上传缩略图
                CompressPic.INSTANCE.compressPic(pressFile.getParent(), pressFile.getParent(), pressFile.getName(), pressFile.getName(), limit_width, limit_height, true);
                pressImgs.add(pressImgFilePath);
            }
        }
        return pressImgs;
    }


    /**
     * 上传为本地临时文件
     *
     * @param inputStreams      spring 文件对象
     * @param toSaveFileNewName 期望保存的文件名 不需要后缀名 [xxxxx]
     * @return
     * @throws Exception
     */
    private java.util.List<String> uploadFileToLocal(InputStream[] inputStreams, String toSaveFilePath, String toSaveFileNewName) throws Exception {
        java.util.List<String> imgnames = new ArrayList<>();
        toSaveFilePath = toSaveFilePath.startsWith("/") ? toSaveFilePath.substring(1) : toSaveFilePath;
        toSaveFilePath = toSaveFilePath.endsWith("/") ? toSaveFilePath : toSaveFilePath + "/";
        int i = 1;//图片名后缀
        for (InputStream inputStream : inputStreams) {
            if (!this.checkFileSize((long) inputStream.available())) {
                return null;
            }
            String fileNewName = toSaveFileNewName.indexOf(".") == -1 ? toSaveFileNewName + "-" + i + ".jpg" : toSaveFileNewName.substring(0, toSaveFileNewName.indexOf(".")) + "-" + i + ".jpg";
            log.info(" fileNewName >> " + fileNewName);
            String filePath = PROJECTPATH + upload_temp_dir + toSaveFilePath;
            log.info(" >> " + filePath);
            File toUploadFile = new File(filePath, fileNewName);
            FileUtils.copyInputStreamToFile(inputStream, toUploadFile);/* 上传文件到临时目录下 */
            imgnames.add(filePath + fileNewName);
            i++;
        }
        return imgnames;
    }


    /**
     * 上传文件到ftp服务器
     *
     * @param fileRelativePaths 文件相对路径 [/xxxx/xxx/yyyyyy.yyy]
     * @return 上传后ftp路径  [http://wwww.xxx.com/xxx/xxx/yyyyyy.yyy]
     * @throws Exception
     */
    private java.util.List<String> uploadFileToFtp(String[] fileRelativePaths) throws Exception {
        try {
            java.util.List<String> fullPaths = new ArrayList<>();
            FTPClient ftpClient = FtpTool.getFtpConnected();
            for (String path : fileRelativePaths) {
                String ftpSavePath = path.replace(PROJECTPATH + upload_temp_dir, "/");
                String localFilePath = path;
                log.info("本地文件 >> " + localFilePath);
                log.info("上传文件到ftp服务器 >> " + ftpSavePath);
                localFilePath = localFilePath.replace("\\", "/");
                localFilePath = localFilePath.startsWith("/") ? localFilePath : "/" + localFilePath;
                boolean flag = false;
                flag = new FtpTool().uploadFile(localFilePath, ftpSavePath, ftpClient);
                if (flag) {
                    if (ftpSavePath.indexOf(IMG_COMPRESSTYPE_SPLIT) == -1) {
                        fullPaths.add(FtpTool.getInstance().combImgUrl(ftpSavePath));
                    }
                }
            }
            ftpClient.disconnect();
            return fullPaths;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获得文件的后缀名
     *
     * @param filename
     * @return
     */

    public static String getfsuffix(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 过滤文件大小限制
     *
     * @param fileSize 文件大小
     * @return true 可以上传 / false 不可以上传
     */
    private boolean checkFileSize(Long fileSize) {
        return fileSize <= Integer.parseInt(ConfigUtil.getValue("upload_img_size", "upload_config.properties")) ? true : false;
    }


    /**
     * 为图片添加水印
     *
     * @param pressImg
     * @param _file
     * @param x
     * @param y
     * @Author BoRong
     * @MethodName pressImage
     */
    private final static void pressImage(String pressImg, File _file, int x, int y) {
        try {
            Image src = ImageIO.read(_file);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);

            // 水印文件
            File _filebiao = new File(pressImg);
            Image src_biao = ImageIO.read(_filebiao);
            int wideth_biao = src_biao.getWidth(null);
            int height_biao = src_biao.getHeight(null);
            /*设置水印的位置坐标*/
            g.drawImage(src_biao, (wideth - wideth_biao - 10), (height - height_biao - 10), wideth_biao, height_biao, null);
            // 水印文件结束
            g.dispose();
            FileOutputStream out = new FileOutputStream(_file.getAbsolutePath());
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(image);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
