/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package com.rzhkj.core;

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
//    public static Env env = DEVELOP;//开发

    /*开启测试环境配置*/
//    public static Env env = SANDBOX;//测试


    /**
     * ***********************************************
     * 开启  [生产环境]  配置  非上线不得开启,非管理员不可操作
     * ***********************************************
     */
    public static Env env = PRODUCT;//生产


}
