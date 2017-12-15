package com.rzhkj.core.interceptor;

import com.rzhkj.core.common.Constants;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.tools.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * LoginInterceptor
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取sessionId
        String sessionid = cookie.getCode((String) Constants.sessionid.val, request);
        logger.info("===> 访问拦截，session key : " + sessionid);
        String studentSessionId = cookie.getCode((String) Constants.Student.val, request);
        String studentCode = request.getParameter("studentCode");


        if (StringUtils.isNotBlank(sessionid)) {
            String ip = IPGetter.getAccessIp();
            String sessionIp = (String) hashOperations.get(sessionid, "ip");
            if (ip.equals(sessionIp)) {
                String sessionCode = (String) hashOperations.get(sessionid, "sessionCode");    //获取用户账户信息

                //防止学生获取其他人信息
                if (studentSessionId != null && studentCode != null) {
                    if (!studentCode.equals(sessionCode)) {
                        return false;
                    }
                }
                if (StringUtils.isNotBlank(sessionCode)) {
                    redisTemplate.expire(sessionid, (Long) Constants.SS_TIMEOUT.val, TimeUnit.MILLISECONDS);   //重置session过期时间
                    return true;
                }
            } else {
                //注销登录凭证
                this.invalidate(response);
            }
        }


        String AssistantSessionId = cookie.getCode((String) Constants.AssistantSessionId.val, request);
        logger.info("===> 访问拦截，AssistantSessionId key : " + AssistantSessionId);

        //后台登陆
        if (StringUtils.isNotBlank(AssistantSessionId)) {
            String ip = IPGetter.getAccessIp();
            String sessionIp = (String) hashOperations.get(AssistantSessionId, "ip");
            if (ip.equals(sessionIp)) {
                String account = (String) hashOperations.get(AssistantSessionId, "sessionCode");    //获取用户账户信息
                if (StringUtils.isNotBlank(account)) {
                    redisTemplate.expire(AssistantSessionId, (Long) Constants.SS_TIMEOUT.val, TimeUnit.MILLISECONDS);   //重置session过期时间
                    return true;
                }
            } else {
                this.invalidateAssiant(response);    //注销登录凭证
            }
        }


        //未登录需要跳转的地址
        String loginUri = "/page/login";
        //如果是ajax请求响应头会有，x-requested-with
        if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Location", loginUri);    //跳转登陆页面
            response.setHeader("serverError", "sessionOut");  //session过期
            response.getWriter().write(JSON.toJSONString(BeanRet.create(false, "登陆已失效，请重新登陆！")));
        }
        return false;
    }


    private static CookieTools cookie = CookieTools.INSTANCE;

    /**
     * 注销登录凭证
     */
    public void invalidate(HttpServletResponse response) {
        cookie.delCookie((String) Constants.sessionid.val, Constants.Domain, "/", response);
        cookie.delCookie(Constants.Student.name(), Constants.Domain, "/", response);
        cookie.delCookie(Constants.Tutor.name(), Constants.Domain, "/", response);
    }

    /**
     * 注销登录凭证
     */
    public void invalidateAssiant(HttpServletResponse response) {
        cookie.delCookie(Constants.AssistantSessionId.name(), Constants.Domain, "/", response);
        cookie.delCookie(Constants.Assitant.name(), Constants.Domain, "/", response);
    }
}