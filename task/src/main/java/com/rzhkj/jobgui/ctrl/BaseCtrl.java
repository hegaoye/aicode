package com.rzhkj.jobgui.ctrl;

import com.alibaba.fastjson.JSON;
import com.rzhkj.base.tools.WebData;
import com.rzhkj.core.tools.CookieTools;
import com.rzhkj.sys.entity.SystemAccount;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 基本ctrl
 * Created by 立坤 on 2017/2/3.
 */
public class BaseCtrl {
    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    protected HashOperations<String, String, Object> hashOperations;
    @Autowired
    protected HttpServletRequest request;

    /**
     * 取得当前登陆用户的信息
     *
     * @return 登陆用户信息对象
     */
    public SystemAccount getAccount() {
        SystemAccount rdAcct = null;
        String acct = getSessionVal("account");    //取得登陆账户
        if (StringUtils.isNotBlank(acct)) {  //若登陆账户存在，则已登录，拼装登陆用户信息；
            String id = getSessionVal("id");
            String account = getSessionVal("account");
            String accountLevel = getSessionVal("accountLevel");
            String state = getSessionVal("state");
            rdAcct = new SystemAccount(Long.parseLong(id), account, accountLevel, state);
        }
        return rdAcct;
    }


    /**
     * 生成当前登陆用户信息
     *
     * @param systemAccount 登陆账户
     */
//    protected void createAccount(SystemAccount systemAccount, HttpServletResponse response) {
//        //初始化session
//        String ssKey = RedisKey.taskSessionKey();   //生成sessionkey
//        if (redisTemplate.hasKey(ssKey)) {    //如果已经有此key，则删除
//            redisTemplate.delete(ssKey);
//        }
//        setSessionVal(ssKey, "id", String.valueOf(systemAccount.getId()));
//        setSessionVal(ssKey, "account", systemAccount.getAccount());
//        setSessionVal(ssKey, "accountLevel", systemAccount.getAccountLevel());
//        setSessionVal(ssKey, "state", systemAccount.getState());
//        redisTemplate.expire(ssKey, WebData.SS_TIMEOUT, TimeUnit.MILLISECONDS);   //设置session过期时间
//        CookieUtils.addCookie(WebData.SS_KEY, ssKey, true, response);    //写入cookie
//    }

    /**
     * 向session中赋值
     *
     * @param name  键
     * @param value 值
     */
    public void setSessionVal(String name, Object value) {
        String ssKey = CookieTools.INSTANCE.getCode(WebData.SS_KEY, request);    //获取sessionKey
        if (StringUtils.isNotBlank(ssKey)) {
            setSessionVal(ssKey, name, value);
        }
    }

    /**
     * 向session中赋值
     *
     * @param ssKey ssKey
     * @param name  键
     * @param value 值
     */
    public void setSessionVal(String ssKey, String name, Object value) {
        if (StringUtils.isNotBlank(ssKey)) {
            hashOperations.put(ssKey, name, value);
        }
    }

    /**
     * 从session中取值
     *
     * @param name 键值
     * @return String类型的值
     */
    public String getSessionVal(String name) {
        String ssKey = CookieTools.INSTANCE.getCode(WebData.SS_KEY, request);    //获取sessionKey
        if (StringUtils.isNotBlank(ssKey)) {
            return (String) hashOperations.get(ssKey, name);
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
    public void invalidateSession() {
        String ssKey = CookieTools.INSTANCE.getCode(WebData.SS_KEY, request);    //获取sessionKey
        if (StringUtils.isNotBlank(ssKey)) {
            redisTemplate.delete(ssKey);    //删除session
        }
    }
}
