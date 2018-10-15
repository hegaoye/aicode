/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.目.
 */
package io.aicode.core.base;

import com.alibaba.fastjson.JSON;
import io.aicode.core.common.Constants;
import io.aicode.core.tools.CookieTools;
import io.aicode.core.tools.redis.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;

public class BaseCtrl implements Serializable {
    private static final long serialVersionUID = 6357869213649815390L;
    protected final static Logger logger = LoggerFactory.getLogger(BaseCtrl.class);

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    protected HashOperations<String, String, Object> hashOperations;
    @Resource
    protected RedisUtils redisUtils;
    @Autowired
    protected HttpServletRequest request;




    /**
     * 向session中赋值
     *
     * @param name  键
     * @param value 值
     */
    public void setSessionVal(String name, Object value) {
        String sesionKey = CookieTools.INSTANCE.getCode((String) Constants.sessionid.val, request);    //获取sessionKey
        if (StringUtils.isNotBlank(sesionKey)) {
            setSessionVal(sesionKey, name, value);
        }
    }

    /**
     * 向session中赋值
     *
     * @param sesionKey sesionKey
     * @param name      键
     * @param value     值
     */
    public void setSessionVal(String sesionKey, String name, Object value) {
        if (StringUtils.isNotBlank(sesionKey)) {
            hashOperations.put(sesionKey, name, value);
        }
    }

    /**
     * 从session中取值
     *
     * @param name 键值
     * @return String类型的值
     */
    public String getSessionVal(String name) {
        String sesionKey = CookieTools.INSTANCE.getCode((String) Constants.sessionid.val, request);    //获取sessionKey
        if (StringUtils.isNotBlank(sesionKey)) {
            return (String) hashOperations.get(sesionKey, name);
        }
        return null;
    }



    /**
     * 从session中取值
     *
     * @param name  键值
     * @param clazz 转换的对象的class
     * @param <T>
     * @return
     */
    public <T> T getSessionVal(String name, Class<T> clazz) {
        return JSON.parseObject(getSessionVal(name), clazz);
    }

    /**
     * 从session中取得List类型的值
     *
     * @param name  键值
     * @param clazz 转换的对象的class
     * @param <T>
     * @return
     */
    public <T> List<T> getSessionAryVal(String name, Class<T> clazz) {
        return JSON.parseArray(getSessionVal(name), clazz);
    }

    /**
     * 注销session
     */
    public void invalidateSession(HttpServletResponse response) {
        String sesionKey = CookieTools.INSTANCE.getCode((String) Constants.sessionid.val, request);    //获取sessionKey
        if (StringUtils.isNotBlank(sesionKey)) {
            CookieTools.INSTANCE.delCookie((String) Constants.sessionid.val, response);
            redisTemplate.delete(sesionKey);    //删除session
        }
    }
}
