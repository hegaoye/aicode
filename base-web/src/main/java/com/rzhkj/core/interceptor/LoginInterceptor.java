package com.rzhkj.core.interceptor;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.rzhkj.core.base.JwtToken;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.tools.CookieTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LoginInterceptor
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            return false;
        }

        boolean flag = JwtToken.verifier(token, "", "");
        if (flag) {
            return true;
        } else {
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
        }
        return false;
    }
}