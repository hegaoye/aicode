package ${basePackage}.core.tools;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public enum CookieTools {
    INSTANCE;

    private final static Logger log = LoggerFactory.getLogger(CookieTools.class);


    /**
     * @param key
     * @return string
     * @Enclosing_Method : getCode
     * @Written by : likun
     * @Creation Date : 2012.10.16
     * @version : v1.00
     * @Description : 获取cookie
     */
    public String getCode(String key) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getCode(key, request);
    }

    /**
     * @param key
     * @param request
     * @return
     * @Enclosing_Method : getCode
     * @Written by : liyanping
     * @Creation Date : Jan 4, 2011 6:26:41 PM
     * @version : v1.00
     * @Description : 获取cookie
     */
    public String getCode(String key, HttpServletRequest request) {
        String code = null;
        try {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("city")) {
                        log.info("cookie[city] 遍历出值：" + cookie.getValue());
                    }
                }
            }

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    // log.info("cookie\tkey['" + key + "']");
                    if (cookie.getName().equals(key)) {
                        code = cookie.getValue();
                        // log.info("cookie\tkey['" + key + "']对就值：" + code);
                    }
                }
            }
            log.info("获取cookie['" + key + "':'" + code + "']");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return code;

    }

    /**
     * @param key
     * @param value
     * @param response
     * @return : void
     * @Enclosing_Method : setCode
     * @Written by : liyanping
     * @Creation Date : Jan 4, 2011 6:27:28 PM
     * @version : v1.00
     * @Description : 设置cookie
     */
    public void setCode(String key, String value, HttpServletResponse response) {
        try {
            Cookie c = new Cookie(key, value);
            c.setPath("/");
            c.setMaxAge(7 * 60 * 60);
//            c.setDomain(".ilinm.com");
            c.setSecure(true);
            log.info("最新设置的cookie['" + key + "':' " + value + "']" + "\t路径为\t'/WEB-INF/web'\tdomain\t");
            response.addCookie(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param key
     * @param value
     * @param day
     * @return : void
     * @Enclosing_Method : setCode
     * @Written by        : wangchuan
     * @Creation Date     : Sep 25, 2012 12:14:34 PM
     * @version : v1.00
     * @Description : 设置cookie过期时间
     */
    public void setCode(String key, String value, int day, HttpServletResponse response) {
        try {
            this.setCodeHour(key, value, day * 24, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置按照时间进行过期处理
     *
     * @param key
     * @param value
     * @param hour
     * @param response
     */
    public void setCodeHour(String key, String value, int hour, HttpServletResponse response) {
        try {
            Cookie c = new Cookie(key, value);
            c.setPath("/");
            c.setMaxAge(hour * 60 * 60);
            response.addCookie(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置按照时间进行过期处理
     *
     * @param key
     * @param value
     * @param mins
     * @param response
     */
    public void setCodeMins(String key, String value, int mins, HttpServletResponse response) {
        try {
            Cookie c = new Cookie(key, value);
            c.setPath("/");
            c.setMaxAge(mins * 60);
            response.addCookie(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建cookie
     *
     * @param name     名称
     * @param value    值
     * @param response response对象
     */
    public void addCookie(String name, String value, HttpServletResponse response) {
        setCookie(name, value, -1, null, "/", response);
    }

    /**
     * 创建cookie
     *
     * @param name     名称
     * @param value    值
     * @param domain   域名
     * @param response response对象
     */
    public void addCookie(String name, String value, String domain, HttpServletResponse response) {
        setCookie(name, value, -1, domain, "/", response);
    }

    /**
     * @param key   cookie名称
     * @param value cookie值
     * @param hours 有效时间单位为 小时
     * @param path  设置cookie路径 不设置默认为当前路径(对于servlet来说为request.getContextPath() +
     *              web.xml 里配置的该Servlet的url-pattern路径部分)
     * @return : void
     * @Enclosing_Method : setCookie
     * @Written by : BoRong
     * @Creation Date : Nov 1, 2011 9:56:54 AM
     * @version : v1.00
     * @Description : 对cookie的设置
     */
    public void setCookie(String key, String value, int hours, String domain, String path, HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie(key, value);
            if (hours > 0) {
                cookie.setMaxAge(hours * 60 * 60);
            } else {
                cookie.setMaxAge(hours);
            }
            cookie.setHttpOnly(false);
            cookie.setPath(path);
            if (StringUtils.isNotEmpty(domain)) {
                cookie.setDomain(domain);
            }

            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param key
     * @param path 设置cookie路径 不设置默认为当前路径(对于servlet来说为request.getContextPath() +
     *             web.xml 里配置的该Servlet的url-pattern路径部分)
     * @return : void
     * @Enclosing_Method : delCookie
     * @Written by : BoRong
     * @Creation Date : Nov 1, 2011 10:06:45 AM
     * @version : v1.00
     * @Description :
     */
    public void delCookie(String key, String pattern, String path, HttpServletResponse response) {
        try {
            Cookie c = new Cookie(key, "");
            c.setMaxAge(0);
            if (StringUtils.isNotEmpty(pattern)) {
                c.setDomain(pattern);
            }
            c.setPath(path);
            response.addCookie(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param key
     * @return : void
     * @Enclosing_Method : delCookie
     * @Written by : BoRong
     * @Creation Date : Nov 1, 2011 10:06:45 AM
     * @version : v1.00
     * @Description :
     */
    public void delCookie(String key, HttpServletResponse response) {
        try {
            Cookie c = new Cookie(key, "");
            c.setMaxAge(0);
//          c.setDomain(pattern);
            c.setPath("/");
            response.addCookie(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param key
     * @param value
     * @param hours
     * @param pattern
     * @param path
     * @param response key, Object value, int hours, String pattern, String path
     *                 ,HttpServletResponse response
     *                 <p>
     *                 函数介绍：更新cookie，传response
     * @return : void
     * @Enclosing_Method : editCookie
     * @Written by : likun
     * @Creation Date : 2012.10.16
     * @version : v1.00
     * @Description :
     */
    public void editCookie(String key, Object value, int hours, String pattern, String path, HttpServletResponse response) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            editCookie(key, value, hours, pattern, path, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param key
     * @param value
     * @param hours
     * @param pattern
     * @param path
     * @param request
     * @param response key, Object value, int hours, String pattern, String path
     *                 ,HttpServletResponse response
     *                 <p>
     *                 函数介绍：更新cookie，传response和request
     * @return : void
     * @Enclosing_Method : editCookie
     * @Written by : likun
     * @Creation Date : 2012.10.16
     * @version : v1.00
     * @Description :
     */
    public void editCookie(String key, Object value, int hours, String pattern, String path, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies.length >= 1) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(key)) {
                    String oldValue = cookies[i].getValue();
                    cookies[i].setValue(value.toString());
                    cookies[i].setPath(path);
                    cookies[i].setDomain(pattern);
                    cookies[i].setMaxAge(hours * 60 * 60);
                    response.addCookie(cookies[i]);
                    break;
                }
            }
        }
    }

    /**
     * 将cookie封装到Map里面
     *
     * @param request
     * @return
     */
    private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

}