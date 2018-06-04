package com.carservice.base;

import com.carservice.core.common.Constants;
import com.carservice.core.tools.*;
import com.carservice.core.tools.redis.RedisUtils;
import com.carservice.rbac.vo.AdminVO;
import com.carservice.store.entity.Store;
import com.carservice.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bobai
 * @date 2018/5/16
 */
public class BaseCtrl {
    /**
     * redis以及cookie 失效时间（单位是秒）
     */
    private static int redis_cache_timeout = Integer.parseInt(ConfigUtil.getValue("redis.cachetimeout", "redis-manager-config.properties"));

    @Resource
    public RedisUtils redisUtils;

    private static CookieTools cookie = CookieTools.INSTANCE;
    protected HttpServletRequest request;

    /**
     * 初始化方法
     *
     * @param request  request对象
     * @param response response对象
     */
    public void init(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        String code = cookie.getCode(Constants.SystemEnum.USER_SSO_LOGININFO.value, request);
        // 当前session
        //根据浏览器cookie获取session
        if (StringTools.isNotEmpty(code)) {
            this.localSession = getLocalSession(false, code);
        }
    }

    /**
     * 得到request对象
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
    /**
     * 本地session
     */
    public Map<String, Object> localSession = new HashMap<String, Object>();

    /**
     * 生成验证凭证
     *
     * @param code
     * @param loginInfo
     * @param response
     */
    public void generSoleTicket(String code, String loginInfo, String systemFlag, HttpServletResponse response) {
        String userIp = IPGetter.getAccessIp();
        String rndCode = UuidTools.getUUIDString();
        try {
            String securityCode = Md5.getMD5Str(code + userIp + rndCode);
            String ticket = securityCode;
            // 保存cookie
            cookie.setCookie(Constants.SystemEnum.getState(systemFlag + "_SSO_LOGININFO").value, loginInfo, redis_cache_timeout * 60 * 60, response);
            cookie.setCookie(Constants.SystemEnum.getState(systemFlag + "_SSO_USERCODE").value, code, redis_cache_timeout * 60 * 60, response);
            cookie.setCookie(Constants.SystemEnum.getState(systemFlag + "_SSO_LGTICKET").value, ticket, redis_cache_timeout * 60 * 60, response);
            redisUtils.set(loginInfo + "_key", rndCode, redis_cache_timeout * 60 * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 向 session 设置属性值
     *
     * @param name
     * @param value
     */
    public void setSession(String name, Object value, String code) {
        LocalSession session = getLocalSession(true, code);
        session.put(name, value);
        // 保存session信息到缓存服务器
        saveHttpSession(session, code);
        // 给当前页面中的session赋值
        this.localSession = session;
    }


    /**
     * 把session放入缓存服务器
     *
     * @param session
     * @param code
     */
    public void saveHttpSession(LocalSession session, String code) {
        if (StringUtils.isNotBlank(code)) {
            redisUtils.set(code, session, redis_cache_timeout * 60 * 60);
        }
    }

    /**
     * 获取session
     *
     * @param isCreate
     * @param code
     * @return
     */
    public LocalSession getLocalSession(boolean isCreate, String code) {
        LocalSession session = (LocalSession) redisUtils.get(code);
        if (session == null) {
            if (isCreate) {
                session = new LocalSession();
            }
        }
        return session;
    }

    /**
     * 获得用户session
     * @return
     */
    public User getSessionUser() {
        String code = cookie.getCode(Constants.SystemEnum.USER_SSO_LOGININFO.value, request);
        if (org.apache.commons.lang.StringUtils.isNotBlank(code)) {
            localSession = getLocalSession(false, code);
            if (localSession != null) {
                User user = (User) localSession.get(Constants.SystemEnum.USER_INFO.value);
                return user;
            }
        }
        return null;
    }

    /**
     * 获得店铺session
     * @return
     */
    public Store getSessionStore() {
        String code = cookie.getCode(Constants.SystemEnum.STORE_SSO_LOGININFO.value, request);
        if (org.apache.commons.lang.StringUtils.isNotBlank(code)) {
            localSession = getLocalSession(false, code);
            if (localSession != null) {
                Store store = (Store) localSession.get(Constants.SystemEnum.STORE_INFO.value);
                return store;
            }
        }
        return null;
    }

    /**
     * 获得登录管理员session
     * @return
     */
    public AdminVO getSessionAdmin() {
        String code = cookie.getCode(Constants.SystemEnum.RBAC_SSO_LOGININFO.value, request);
        if (org.apache.commons.lang.StringUtils.isNotBlank(code)) {
            localSession = getLocalSession(false, code);
            if (localSession != null) {
                AdminVO account = (AdminVO) localSession.get(Constants.SystemEnum.RBAC_INFO.value);
                return account;
            }
        }
        return null;
    }

    public String getSessionUserCode() {
        String code = cookie.getCode(Constants.SystemEnum.USER_SSO_USERCODE.value, request);
        return code;
    }

    public String getSessionStoreCode() {
        String code = cookie.getCode(Constants.SystemEnum.STORE_SSO_USERCODE.value, request);
        return code;
    }

    public String getSessionAdminCode() {
        String code = cookie.getCode(Constants.SystemEnum.RBAC_SSO_USERCODE.value, request);
        return code;
    }

    /**
     * 注销用戶登录session以及登录凭证
     *
     * @param response
     */
    public void invalidate(HttpServletResponse response, String systemFlag) {
        // 注销session
        String loginInfo = cookie.getCode(Constants.SystemEnum.getState(systemFlag + "_SSO_LOGININFO").value);
        redisUtils.del(loginInfo);
        redisUtils.del(loginInfo + "_key");
        // 注销cookie
        cookie.delCookie(Constants.SystemEnum.getState(systemFlag + "_SSO_USERCODE").value, response);
        cookie.delCookie(Constants.SystemEnum.getState(systemFlag + "_SSO_LGTICKET").value, response);
        cookie.delCookie(Constants.SystemEnum.getState(systemFlag + "_SSO_LOGININFO").value, response);
    }


}
