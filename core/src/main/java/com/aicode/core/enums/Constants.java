/*
 *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.
 */

package com.aicode.core.enums;

/**
 * 常量表
 */
public enum Constants {
    sessionid("sessionId"),//登陆session key
    SS_TIMEOUT(360000L * 1000 * 4), //session过期时间(毫秒)
    Cookie_Maxage(5),//cookie 最大过期
    AccountCode("accountCode"),//jwt 的key
    ;

    public Object val;

    Constants(Object val) {
        this.val = val;
    }

    public static String Domain = null;
}