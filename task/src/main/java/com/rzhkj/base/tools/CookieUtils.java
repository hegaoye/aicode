package com.rzhkj.base.tools;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author IntelliJ IDEA.
 * @author lixin 2016-08-13 10:47
 */

public class CookieUtils {
    /**
     * 创建cookie
     *
     * @param name     名称
     * @param value    值
     * @param response response对象
     */
    public static void addCookie(String name, String value, HttpServletResponse response) {
        addCookie(name, value, -1, null, null, false, response);
    }

    /**
     * 创建cookie
     *
     * @param name     名称
     * @param value    值
     * @param maxAge   cookie生命(秒),设置-1则为浏览器进程Cookie(内存中保存)，关闭浏览器就失效
     * @param response response对象
     */
    public static void addCookie(String name, String value, int maxAge, HttpServletResponse response) {
        addCookie(name, value, maxAge, null, null, false, response);
    }

    /**
     * 创建cookie
     *
     * @param name     名称
     * @param value    值
     * @param httpOnly 是否是只能通过http请求读取；若设置为ture,则js等其他读取方式无法读取cookie
     * @param response response对象
     */
    public static void addCookie(String name, String value, boolean httpOnly, HttpServletResponse response) {
        addCookie(name, value, -1, null, null, httpOnly, response);
    }

    /**
     * 创建cookie
     *
     * @param name     名称
     * @param value    值
     * @param maxAge   cookie生命(秒),设置-1则为浏览器进程Cookie(内存中保存)，关闭浏览器就失效
     * @param httpOnly 是否是只能通过http请求读取；若设置为ture,则js等其他读取方式无法读取cookie
     * @param response response对象
     */
    public static void addCookie(String name, String value, int maxAge, boolean httpOnly, HttpServletResponse response) {
        addCookie(name, value, maxAge, null, null, httpOnly, response);
    }

    /**
     * 创建cookie
     *
     * @param name     名称
     * @param value    值
     * @param maxAge   cookie生命(秒),设置-1则为浏览器进程Cookie(内存中保存)，关闭浏览器就失效
     * @param path     设置路径，这个路径即该工程下都可以访问该cookie 如果不设置路径，那么只有设置该cookie路径及其子路径可以访问
     * @param domain   设置cookie域；若设置，如：hmg.com.则此非此域下的网站不能获取cookie,符合此域的如a.hmg.com,b.hmg.com都可获得此cookie
     * @param response response对象
     */
    public static void addCookie(String name, String value, int maxAge, String path, String domain, HttpServletResponse response) {
        addCookie(name, value, maxAge, path, domain, false, response);
    }

    /**
     * 创建cookie
     *
     * @param name     名称
     * @param value    值
     * @param maxAge   cookie生命(秒),设置-1则为浏览器进程Cookie(内存中保存)，关闭浏览器就失效
     * @param path     设置路径，这个路径即该工程下都可以访问该cookie 如果不设置路径，那么只有设置该cookie路径及其子路径可以访问
     * @param domain   设置cookie域；若设置，如：hmg.com.则此非此域下的网站不能获取cookie,符合此域的如a.hmg.com,b.hmg.com都可获得此cookie
     * @param httpOnly 是否是只能通过http请求读取；若设置为ture,则js等其他读取方式无法读取cookie
     * @param response response对象
     */
    public static void addCookie(String name, String value, int maxAge, String path, String domain, boolean httpOnly, HttpServletResponse response) {
        if (StringUtils.isBlank(path)) path = "/";
        Cookie cookie = new Cookie(name, value);
        //cookie.setHttpOnly(httpOnly);
        cookie.setPath(path);
        if (StringUtils.isNotBlank(domain)) cookie.setDomain(domain);
        if (maxAge > 0) cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 根据名字获取cookie
     *
     * @param request request对象
     * @param name    cookie名字
     * @return cookie对象
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = ReadCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    /**
     * 根据名字获取cookie的值
     *
     * @param request request对象
     * @param name    cookie名字
     * @return cookie值
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        String val = null;
        Cookie cookie = getCookie(request, name);
        if (cookie != null) {
            val = cookie.getValue();
        }
        return val;
    }

    /**
     * 删除cookie
     *
     * @param name     cookie名字
     * @param request  request对象
     * @param response response对象
     */
    public static void delCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * 将cookie封装到Map里面
     *
     * @param request
     * @return
     */
    private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = Maps.newHashMap();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

}
