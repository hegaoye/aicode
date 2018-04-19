package ${basePackage}.core;

/**
 * 开发环境，测试环境，生产环境切换
 * 当 public static Env env = PRODUCT;则为生产环境
 * 默认为开发环境，
 * 生产环境将自动启用所有生产配置，
 * 谨慎配置，
 * 非生产环境不要开启
 *
 * @author lixin on 2016/11/16 0016.
 */
public enum Env {
    DEVELOP/*开发环境*/,
    SANDBOX/*测试环境*/,
    PRODUCT/*生产环境*/;


    /*开启开发环境配置*/
    public static Env env = DEVELOP;//开发

    /*开启测试环境配置*/
//    public static Env env = SANDBOX;//测试


    /**
     * ***********************************************
     * 开启  [生产环境]  配置  非上线不得开启,非管理员不可操作
     * ***********************************************
     */
//    public static Env env = PRODUCT;//生产


}
