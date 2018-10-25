/*
 *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.
 */
package io.aicode.base.interceptor;

import com.alibaba.fastjson.JSON;
import io.aicode.base.core.BeanRet;
import io.aicode.base.tools.CookieUtils;
import io.aicode.base.tools.WebData;
import io.aicode.core.tools.CookieTools;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 登陆验证拦截器
 *
 * @date 2014年12月18日 下午2:26:17
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;
    /**
     * 免登入 免检查地址
     */
    private List<String> uncheckUrls;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取参数路径
        String queryString = request.getQueryString();
        String requestUrl = request.getRequestURI() + (queryString != null ? "?" + queryString : "");
        if (uncheckUrls.contains(requestUrl)) {
            return true;
        }
        //获取登录标识
        String ssKey = CookieTools.INSTANCE.getCode(WebData.SS_KEY,request);    //获取sessionId
        try {
            if (StringUtils.isNotBlank(ssKey)) {
                String custno = (String) hashOperations.get(ssKey, "account");    //获取用户账户信息
                if (StringUtils.isNotBlank(custno)) {
                    redisTemplate.expire(ssKey, WebData.SS_TIMEOUT, TimeUnit.MILLISECONDS);   //重置session过期时间
                    return true;
                } else {
                    CookieUtils.delCookie(WebData.SS_KEY, request, response);  //删除本地cookie
                }
            }
        } catch (Exception e) {
            if (StringUtils.isNotBlank(ssKey)) {
                CookieUtils.delCookie(WebData.SS_KEY, request, response);  //删除本地cookie
            }
        }
        //未登录需要跳转的地址
        String loginUri = request.getContextPath() + "/#/page/login";
        //如果是ajax请求响应头会有，x-requested-with
        response.getWriter().write(JSON.toJSONString(BeanRet.create(false, "登陆已失效，请重新登陆{{TOLOGIN}}！", loginUri)));
        return false;
    }

    public List<String> getUncheckUrls() {
        return uncheckUrls;
    }

    public void setUncheckUrls(List<String> uncheckUrls) {
        this.uncheckUrls = uncheckUrls;
    }
}