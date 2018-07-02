package ${basePackage}.core.tools;

import ${basePackage}.core.common.Constants;
import ${basePackage}.core.tools.memcached.MemCachedUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class HttpSessionWrapper {
    // 缓存工具类
    private MemCachedUtil cachedUtil = MemCachedUtil.getInstance();
    // cookie操作类
    private CookieTools doCookie = CookieTools.INSTANCE;
    private String sessionInValidateTime = ConfigUtil.getValue("cache_session_MaxAge");
    private static HttpSessionWrapper instance;

    private HttpSessionWrapper() {
    }

    public static HttpSessionWrapper getInstance() {
        if (instance == null) {
            instance = new HttpSessionWrapper();
        }
        return instance;
    }

    /**
     * @return : void
     * @Enclosing_Method : setSession
     * @Written by : lixin
     * @Creation Date : 2012-2-2 下午04:33:21
     * @version : v1.00
     * @Description : 把session放入缓存服务器
     */
    @SuppressWarnings("unchecked")
    public void saveHttpSession(Map session) {
        // 取出cookie中的userId
        String code = doCookie.getCode((String) Constants.sessionid.val);
        saveHttpSession(session, code);
    }

    /**
     * @param session
     * @param code
     * @return : void
     * @Enclosing_Method : saveHttpSession
     * @Written by : lixin
     * @Creation Date : 2012-2-17 下午02:02:02
     * @version : v1.00
     * @Description : 把session放入缓存服务器
     */
    public void saveHttpSession(Map session, String code) {
        if (StringUtils.isNotBlank(code)) {
            cachedUtil.setCache(code, session, MemCachedUtil.TimeOut.twelveHours);
        }
    }

    /**
     * @return : LocalSession
     * @Enclosing_Method : getLocalSession
     * @Written by : lixin
     * @Creation Date : 2012-7-20 下午03:23:30
     * @version : v1.00
     * @Description : 取得LocalSession
     */
    public LocalSession getLocalSession() {
        String code = doCookie.getCode((String) Constants.sessionid.val);
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return getLocalSession(true, code);
    }

    /**
     * @param isCreate code
     * @return
     * @Enclosing_Method : getLocalSession
     * @Written by : lixin
     * @Creation Date : 2012-7-20 下午05:53:18
     * @version : v1.00
     * @Description : 取得localSession
     */
    public LocalSession getLocalSession(boolean isCreate) {
        String code = doCookie.getCode((String) Constants.sessionid.val);
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return getLocalSession(isCreate, code);
    }

    /**
     * @param isCreate code
     * @return
     * @Enclosing_Method : getLocalSession
     * @Written by : lixin
     * @Creation Date : 2012-7-20 下午05:53:18
     * @version : v1.00
     * @Description : 取得localSession
     */
    public LocalSession getLocalSession(boolean isCreate, String code) {
        LocalSession session = (LocalSession) cachedUtil.get(code);
        if (session == null) {
            if (isCreate) {
                session = new LocalSession();
            }
        }
        return session;
    }

    /**
     * @param isCreate
     * @param request
     * @return
     * @Enclosing_Method : getLocalSession
     * @Written by : lixin
     * @Creation Date : 2012-7-21 下午02:38:39
     * @version : v1.00
     * @Description : 取得localSession
     */
    public LocalSession getLocalSession(boolean isCreate, HttpServletRequest request) {
        String code = doCookie.getCode((String) Constants.sessionid.val, request);
        if (StringUtils.isBlank(code)) {
            return null;
        }

        LocalSession session = getLocalSession(isCreate, code);
        return session;
    }

    /**
     * @param code
     * @return
     * @Enclosing_Method : delSession
     * @Written by : lixin
     * @Creation Date : 2012-2-16 下午05:38:36
     * @version : v1.00
     * @Description : 删除缓存session
     */
    public boolean delSession(String code) {
        boolean flag = false;
        flag = cachedUtil.deleteCache(code);

        return flag;
    }

    /**
     * 设置自定义内容到memcache中缓存使用key为键，time为有效时间以秒为单位，val为缓存内容
     *
     * @param key
     * @param params
     * @param time
     */
    public void setObjToMem(String key, Map<String, Object> params, MemCachedUtil.TimeOut time) {
        cachedUtil.setCache(key, params, time);

    }

    /**
     * @param key 获得缓存中的键
     * @return
     */
    public Map getObjFromMem(String key) {
        Map map = (Map) cachedUtil.get(key);
        return map;

    }

    /**
     *
     * @Enclosing_Method : httpSessionParseMap
     * @Written by : lixin
     * @Creation Date : 2012-2-17 上午11:12:00
     * @version : v1.00
     * @Description : 将httpSession转换成map
     * @return : Map
     *
     * @return
     *
     */
    // private static Map<String,Object> httpSessionParseMap(LocalSession
    // session){
    // Map<String,Object> map = new HashMap<String, Object>();
    // if(session!=null){
    // String[] keys = session.getValueNames();
    // String key;
    // Object value;
    // for(int i=0;i<keys.length;i++){
    // key = keys[i];
    // value = session.getAttribute(key);
    // map.put(key, value);
    // }
    // map.put("JSESSIONID",session.getId());
    // }
    // return map;
    // }

}
