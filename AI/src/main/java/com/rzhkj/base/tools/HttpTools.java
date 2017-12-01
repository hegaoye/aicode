package com.rzhkj.base.tools;

import javax.servlet.http.HttpServletRequest;

/**
 * HTTP请求响应相关工具，用来获取session、cookies、session等，以及相关处理
 *
 * @author yangxiao 2016-08-09 18:22
 */
public class HttpTools {

    /**
     * 获取客户端的真实IP，如果使用了高级匿名代理，则返回的是代理IP
     *
     * @param request HttpServletRequest请求
     * @return 客户端的真实IP
     */
    public static String clientIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
