/*
 *
 *                        http://www.aicode.io
 *
 *
 *       本代码仅用于AI-Code.目.
 */

package io.aicode.core.tools;

import io.aicode.core.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * 图路径管理工具
 */
public class PathTools {
    private final static Logger log = LoggerFactory.getLogger(PathTools.class);
    private static String root;//所有上传文件的存储根路径
    private static String service;//图片服务器地址
    private static String coursewareService;//课件服务器地址

    static {
        InputStream inputStream = PathTools.class.getClassLoader().getResourceAsStream("path.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
            switch (Env.env) {
                case DEVELOP:
                    log.info("----------图片访问路径 开启开发环境-------------");
                    service = p.getProperty("SERVICE_DEV");
                    coursewareService = p.getProperty("CourseWareService_DEV");
                    log.info("图片访问路径::" + service);
                    log.info("----------图片访问路径 开启开发环境-------------");
                    break;
                case SANDBOX:
                    log.info("----------图片访问路径 开启测试环境-------------");
                    service = p.getProperty("SERVICE_SANDBOX");
                    log.info("图片访问路径::" + service);
                    coursewareService = p.getProperty("CourseWareService_SANDBOX");
                    log.info("----------图片访问路径 开启测试环境-------------");
                    break;
                case PRODUCT:
                    log.info("----------图片访问路径 开启生产环境-------------");
                    service = p.getProperty("SERVICE_PRODUCT");
                    coursewareService = p.getProperty("CourseWareService_PRODUCT");
                    log.info("图片访问路径::" + service);
                    log.info("----------图片访问路径 开启生产环境-------------");
                    break;
            }
            root = p.getProperty("ROOT");
        } catch (IOException e) {
            log.error("系统初始化错误，读取配置参数错误！");
        }
    }


    /**
     * 绝对路径
     *
     * @param picPath 图片路径
     * @return 相对基本路径 [http://xxxx/picPath.jpg]
     */
    public static String getAbsolute(String picPath) {
        return service + (picPath.startsWith("/") ? picPath : "/" + picPath);
    }


    /**
     * 课件绝对路径
     *
     * @param coursewarePath 图片路径
     * @return 相对基本路径 [http://xxxx/coursewarePath.html]
     */
    public static String getCourseWareAbsolute(String coursewarePath) {
        return coursewareService + (coursewarePath.startsWith("/") ? coursewarePath : "/" + coursewarePath);
    }


    /**
     * 绝对路径
     *
     * @param code 编码
     * @return 相对基本路径 [http://xxxx/basic/xxxxx/]
     */
    public static String getBasicAbsolute(String code) {
        return service + getBasicRelative(code);
    }

    /**
     * 相对基本文件路径
     *
     * @param code 用户编码
     * @return 相对基本路径 [/basic/xxxxx/]
     */
    public static String getBasicRelative(String code) {
        return code.endsWith("/") ? "/basic/" + code : "/basic/" + code + "/";
    }


    /**
     * 相对课件文件路径
     *
     * @param code 用户编码
     * @return 相对基本路径 [/courseware/xxxxx/]
     */
    public static String getCourseWareRelative(String code) {
        return code.endsWith("/") ? "/courseware/" + code : "/courseware/" + code + "/";
    }

    /**
     * 相对临时本地目录
     *
     * @return 相对基本路径 [/local/14位时间戳/]
     */
    public static String getLocalRelative() {
        return "/local/" + DateTools.dateToNum14(new Date()) + "/";
    }


    /**
     * 获得身份证生成绝对路径
     *
     * @param idcard 身份证号
     * @return 绝对路径 [http://xxxx/xxx/xx/xx/]
     */
    public static String getIdcardAbsolute(String idcard) {
        return service + getIdcardRelative(idcard);
    }

    /**
     * 获得身份证生成相对路径
     *
     * @param idcard
     * @return 相对路径[/xx/xx/xx/]
     */
    public static String getIdcardRelative(String idcard) {
        String province = idcard.substring(0, 2);
        String city = idcard.substring(2, 4);
        String county = idcard.substring(4, 6);
        String year = idcard.substring(6, 10);
        String month = idcard.substring(10, 12);
        return "/Basic/" + province + "/" + city + "/" + county + "/" + year + "/" + month + "/";
    }

    public static String getRoot() {
        return root;
    }

    public static String getService() {
        return service;
    }


}