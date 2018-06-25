/*
 * ${copyright}
 */
package ${basePackage}.core.tools.memcached;

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

}