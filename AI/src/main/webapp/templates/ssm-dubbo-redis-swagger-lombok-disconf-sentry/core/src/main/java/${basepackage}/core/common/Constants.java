/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package ${basePackage}.core.common;

import ${basePackage}.core.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常量表
 */
public enum Constants {
    sessionid("sessionId"),//登陆session key
    AssistantSessionId("AssistantSessionId"),//登陆session key
    SS_TIMEOUT(360000L * 1000 * 4), //session过期时间(毫秒)
    Cookie_Maxage(5),//cookie 最大过期
    Tutor("Tutor"),//sessionType
    Student("Student"),//sessionType
    Assitant("Assitant"),//sessionType
    ;

    public Object val;

    Constants(Object val) {
        this.val = val;
    }

    private final static Logger log = LoggerFactory.getLogger(Constants.class);

    public static String Domain = null;
    public static String AssistantDomain = null;


    static {
        switch (Env.env) {
            case DEVELOP:
                log.info("----------常量表 开启开发环境-------------");
                Domain = null;
                AssistantDomain = null;
                log.info("===> 域名" + Domain);
                log.info("----------常量表 开启开发环境-------------");
                break;
            case SANDBOX:
                log.info("----------常量表 开启测试环境-------------");
                Domain = null;
                AssistantDomain = null;
                log.info("===> 域名" + Domain);
                log.info("----------常量表 开启测试环境-------------");
                break;
            case PRODUCT:
                log.info("----------常量表 开启生产环境-------------");
                Domain = "ponddy.com";
                AssistantDomain = "my-tutors.ponddy.com";
                log.info("===> 域名" + Domain.toString());
                log.info("----------常量表 开启生产环境-------------");
                break;
        }
    }
}