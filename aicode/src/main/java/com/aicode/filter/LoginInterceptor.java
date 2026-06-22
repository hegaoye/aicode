package com.aicode.filter;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.core.tools.JwtToken;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 登录拦截器。
 * <p>token 解析优先级：</p>
 * <ol>
 *   <li>请求头 {@code Authorization: Bearer <token>}</li>
 *   <li>URL query {@code ?token=<token>}（兼容现状）</li>
 * </ol>
 * <p>任一来源命中且 JWT 验签通过则放行；否则返回 401 + Session_Out。</p>
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = extractToken(request);
        if (StringUtils.isNotEmpty(token) && JwtToken.verifier(token)) {
            return true;
        }

        //未登录需要跳转的地址
        String loginUri = "/page/login";
        //如果是ajax请求响应头会有，x-requested-with
        if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with")
                .equalsIgnoreCase("XMLHttpRequest")) {
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Location", loginUri);    //跳转登陆页面
            response.setHeader("serverError", "sessionOut");  //session过期
            response.getWriter().write(JSON.toJSONString(R.failed(BaseException.BaseExceptionEnum.Session_Out)));
        }
        return false;
    }

    /**
     * 优先取 Header（推荐），其次 URL query（兼容老接口与 .opencode skill）。
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (StringUtils.isNotEmpty(header)) {
            String trimmed = header.trim();
            if (trimmed.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
                return trimmed.substring(BEARER_PREFIX.length()).trim();
            }
            return trimmed;
        }
        return request.getParameter("token");
    }
}