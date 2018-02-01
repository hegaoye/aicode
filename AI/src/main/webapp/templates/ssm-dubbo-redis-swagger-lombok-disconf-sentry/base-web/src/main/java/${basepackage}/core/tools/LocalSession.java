package ${basePackage}.core.tools;


import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @version : v1.00
 * @Program Name : mds_portal.com.rzhkj.rbac.portal.common.LocalSession.java
 * @Copyright : www.rzhkj.com
 * @Written by : lixin
 * @Creation Date : 2012-7-18 上午09:49:49
 * @Description : 本地session
 * @ModificationHistory Who When What -------- ----------
 * ------------------------------------------------
 * lixin 2012-7-18
 */
public class LocalSession extends AbstractMap<String, Object> implements Serializable {

    /**
     * serialVersionUID:
     *
     * @since Ver 1.0
     */

    private static final long serialVersionUID = -5907804954783454349L;

    private String id;
    //session标识（在action外使用localSession，则需设定）
    private Map<String, Object> map = new HashMap<String, Object>();

    /**
     * 构造函数：LocalSession.
     */
    public LocalSession() {
        this.id = UuidTools.getUUIDString();
    }

    /**
     * 构造函数：LocalSession.
     *
     * @param id
     */
    public LocalSession(String id) {
        this.id = id;
    }

    /**
     * @param key
     * @param value
     * @return
     * @Enclosing_Method : put
     * @Written by : lixin
     * @Creation Date : 2012-7-20 上午09:17:05
     * @version : v1.00
     * @Description : 保存属性,同时返回保存的值
     */
    public Object put(String key, Object value) {
//		logger.info("put....key.."+key+"...value..."+value);
        synchronized (map) {
            //只允许保存null或序列化的对象
            if (value instanceof Serializable || value == null) {

                map.put(key.toString(), value);
                return get(key);
            }
            return null;
        }
    }

    /**
     * @param key
     * @param value
     * @return
     * @Enclosing_Method : put
     * @Written by : lixin
     * @Creation Date : 2012-7-20 上午09:17:05
     * @version : v1.00
     * @Description : 保存属性,同时返回保存的值
     */
    public Object put(String key, Object value, String code) {
//		logger.info("put....key.."+key+"...value..."+value+"...code..."+code);
        synchronized (map) {
            //只允许保存null或序列化的对象
            if (value instanceof Serializable || value == null) {

                map.put(key.toString(), value);
                return get(key);
            }
            return null;
        }
    }

    /**
     * @param key
     * @return
     * @Enclosing_Method : get
     * @Written by : lixin
     * @Creation Date : 2012-7-20 上午09:19:16
     * @version : v1.00
     * @Description : 返回属性值
     */
    public Object get(Object key) {
        if (map == null) {
            return null;
        }

        synchronized (map) {
            return map.get(key.toString());
        }
    }

    /**
     * @param key
     * @return
     * @Enclosing_Method : remove
     * @Written by : lixin
     * @Creation Date : 2012-7-20 上午09:23:17
     * @version : v1.00
     * @Description : 移除属性
     */
    public Object remove(Object key) {
        if (map == null) {
            return null;
        }

        synchronized (map) {
            Object value = get(key);
            map.remove(key.toString());

            return value;
        }
    }

    /**
     * @param key
     * @return
     * @Enclosing_Method : containsKey
     * @Written by : lixin
     * @Creation Date : 2012-7-20 上午09:25:43
     * @version : v1.00
     * @Description : 检测是否此键对应的值是否存在
     */
    public boolean containsKey(Object key) {
        if (map == null) {
            return false;
        }

        synchronized (map) {
            return map.containsKey(key);
        }
    }

    /**
     * @return : void
     * @Enclosing_Method : clear
     * @Written by : lixin
     * @Creation Date : 2012-7-20 上午09:28:26
     * @version : v1.00
     * @Description : 清除所有值
     */
    public void clear() {
        if (map == null) {
            return;
        }

        synchronized (map) {
            map.clear();
        }
    }

    /**
     * @Enclosing_Method : invalidate
     * @Written by        : lixin
     * @Creation Date     : 2012-7-20 下午02:47:26
     * @version : v1.00
     * @Description : 注销session
     * @return             : void
     */
    public void invalidate() {
        if (map == null) {
            return;
        }

        synchronized (map) {
            clear();
        }
    }

    /**
     * @Enclosing_Method : entrySet
     * @Written by        : lixin
     * @Creation Date     : 2012-7-20 下午03:33:52
     * @version : v1.00
     * @Description : 转化为Set
     * @return             : Set<Entry<String,Object>>
     */
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public String getId() {
        return id;
    }
}
